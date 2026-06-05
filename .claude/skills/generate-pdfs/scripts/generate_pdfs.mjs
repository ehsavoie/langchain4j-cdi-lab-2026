#!/usr/bin/env node
/**
 * PDF generator for langchain4j-cdi-lab-2026
 *
 * Usage: node generate_pdfs.mjs [readme|workshop|introduction|slides|all]
 *
 * Techniques used:
 *  - README    : npx md-to-pdf
 *  - Workshop/Introduction : Puppeteer + CSS fix (header first-page only) + page-number footer
 *  - Slides    : Puppeteer + evaluateOnNewDocument to patch Reveal.initialize()
 *                so pdfSeparateFragments:false takes effect (one page per slide)
 *                + post-processing to add clickable video links (URI annotations)
 */

import { execSync } from 'child_process';
import { existsSync, readFileSync, writeFileSync, mkdtempSync, rmSync } from 'fs';
import { resolve, dirname, basename } from 'path';
import { fileURLToPath } from 'url';
import { tmpdir } from 'os';

const __dirname = dirname(fileURLToPath(import.meta.url));
const PROJECT_ROOT = resolve(__dirname, '../../../..');

const VIDEO_BASE_URL = 'https://github.com/ehsavoie/langchain4j-cdi-lab-2026/raw/refs/heads/agentic/slides';

// Locate puppeteer: try local /tmp install first, then global
async function loadPuppeteer() {
  const localPaths = [
    '/tmp/node_modules/puppeteer/lib/esm/puppeteer/puppeteer.js',
    '/tmp/node_modules/puppeteer/lib/puppeteer/puppeteer.js',
  ];
  for (const localPath of localPaths) {
    if (existsSync(localPath)) {
      const mod = await import(localPath);
      return mod.default;
    }
  }
  try {
    const mod = await import('puppeteer');
    return mod.default;
  } catch {
    console.error('Puppeteer not found. Install it with: cd /tmp && npm install puppeteer');
    process.exit(1);
  }
}

// Locate pdf-lib
async function loadPdfLib() {
  const localPath = '/tmp/node_modules/pdf-lib/cjs/index.js';
  if (existsSync(localPath)) {
    const mod = await import(localPath);
    return mod;
  }
  try {
    return await import('pdf-lib');
  } catch {
    console.error('pdf-lib not found. Install it with: cd /tmp && npm install pdf-lib');
    process.exit(1);
  }
}

// ── Video helpers ────────────────────────────────────────────────────────────

function extractPosterFrame(webmPath, pngPath) {
  execSync(
    `ffmpeg -y -i "${webmPath}" -vf "select=eq(n\\,30)" -frames:v 1 "${pngPath}"`,
    { stdio: 'pipe' }
  );
}

/**
 * Add clickable Link annotations on video pages pointing to GitHub-hosted videos.
 * The link covers the area where the poster thumbnail is displayed.
 */
async function addVideoLinksInPdf(pdfPath, videoPages) {
  if (!videoPages || videoPages.length === 0) return;

  const pdfLib = await loadPdfLib();
  const { PDFDocument, PDFName, PDFArray, PDFNumber, PDFString } = pdfLib;

  const pdfBytes = readFileSync(pdfPath);
  const doc = await PDFDocument.load(pdfBytes);
  const ctx = doc.context;
  const pages = doc.getPages();

  for (const { pageIndex, webmName, label } of videoPages) {
    if (pageIndex < 0 || pageIndex >= pages.length) {
      console.warn(`  Skipping video "${label}": page index ${pageIndex} out of range`);
      continue;
    }

    const videoUrl = `${VIDEO_BASE_URL}/${webmName}`;
    console.log(`  Linking "${label}" on page ${pageIndex + 1} → ${videoUrl}`);

    const page = pages[pageIndex];
    const { width, height } = page.getSize();

    // Link rect: centered, occupying 80% of the page
    const margin = 0.1;
    const rectX = width * margin;
    const rectY = height * margin;
    const rectW = width * (1 - 2 * margin);
    const rectH = height * (1 - 2 * margin);

    // URI action
    const uriAction = ctx.obj({
      Type: PDFName.of('Action'),
      S: PDFName.of('URI'),
      URI: PDFString.of(videoUrl),
    });

    // Link annotation — transparent, covers the poster area
    const linkAnnotRef = ctx.register(
      ctx.obj({
        Type: PDFName.of('Annot'),
        Subtype: PDFName.of('Link'),
        Rect: [rectX, rectY, rectX + rectW, rectY + rectH],
        Border: [0, 0, 0],
        A: uriAction,
        F: PDFNumber.of(4),
      })
    );

    // Add annotation to page's Annots array
    const pageDict = page.node;
    let annots = pageDict.get(PDFName.of('Annots'));
    if (!annots) {
      annots = ctx.obj([]);
      pageDict.set(PDFName.of('Annots'), annots);
    }
    const annotsResolved = ctx.lookup(annots);
    if (annotsResolved instanceof PDFArray) {
      annotsResolved.push(linkAnnotRef);
    }
  }

  const enrichedBytes = await doc.save();
  writeFileSync(pdfPath, enrichedBytes);
  console.log(`  Video links added to ${basename(pdfPath)}`);
}

// ── README ────────────────────────────────────────────────────────────────────

function generateReadme() {
  const src = resolve(PROJECT_ROOT, 'README.md');
  const out = resolve(PROJECT_ROOT, 'README.pdf');
  console.log('Generating README.pdf...');
  execSync(
    `npx md-to-pdf "${src}" --launch-options '{"executablePath":"/usr/bin/google-chrome","args":["--no-sandbox","--disable-setuid-sandbox"]}'`,
    { stdio: 'inherit' }
  );
  console.log(`  → ${out}`);
}

// ── Generic HTML page (workshop / introduction) ───────────────────────────────

async function generateHtmlPage(puppeteer, { htmlFile, outputFile, label }) {
  const src = resolve(PROJECT_ROOT, htmlFile);
  const out = resolve(PROJECT_ROOT, outputFile);
  console.log(`Generating ${outputFile}...`);

  const browser = await puppeteer.launch({
    headless: true,
    executablePath: '/usr/bin/google-chrome',
    args: ['--no-sandbox', '--disable-setuid-sandbox'],
  });

  try {
    const page = await browser.newPage();

    // Fix sticky header: make it flow on the first page only in print
    await page.addStyleTag({
      content: `
        @media print {
          header, .nav-header, nav, [role="banner"] {
            position: relative !important;
          }
        }
      `,
    });

    await page.goto(`file://${src}`, { waitUntil: 'networkidle0', timeout: 30000 });

    // Inject print-CSS override after navigation too (for dynamically inserted styles)
    await page.addStyleTag({
      content: `
        @media print {
          header, .nav-header, nav, [role="banner"] {
            position: relative !important;
          }
        }
      `,
    });

    await page.pdf({
      path: out,
      format: 'A4',
      printBackground: true,
      displayHeaderFooter: true,
      headerTemplate: '<span></span>', // empty — suppresses browser's default URL/date header
      footerTemplate: `
        <div style="font-size:9px;color:#666;text-align:center;width:100%;margin:0 auto;padding:0 10mm;">
          <span class="pageNumber"></span> / <span class="totalPages"></span>
        </div>`,
      margin: { top: '10mm', bottom: '15mm', left: '10mm', right: '10mm' },
    });

    console.log(`  → ${out}`);
  } finally {
    await browser.close();
  }
}

// ── Reveal.js presentation (slides / introduction) ────────────────────────────

async function generateRevealDeck(puppeteer, { htmlFile, outputFile }) {
  const src = resolve(PROJECT_ROOT, htmlFile);
  const out = resolve(PROJECT_ROOT, outputFile);
  const slidesDir = dirname(src);
  console.log(`Generating ${outputFile}...`);

  // Extract poster frames from videos
  const tmpDir = mkdtempSync(resolve(tmpdir(), 'pdf-videos-'));
  const videoFiles = [];

  const htmlContent = readFileSync(src, 'utf-8');
  const videoRegex = /<video[^>]*>[\s\S]*?<source\s+src="([^"]+\.webm)"/g;
  let match;
  while ((match = videoRegex.exec(htmlContent)) !== null) {
    const webmName = match[1];
    const webmPath = resolve(slidesDir, webmName);
    if (existsSync(webmPath)) {
      const label = basename(webmName, '.webm');
      const posterPath = resolve(tmpDir, `${label}.png`);
      console.log(`  Extracting poster from ${webmName}...`);
      extractPosterFrame(webmPath, posterPath);
      videoFiles.push({ webmName, label, posterPath });
    }
  }

  const browser = await puppeteer.launch({
    headless: true,
    executablePath: '/usr/bin/google-chrome',
    args: ['--no-sandbox', '--disable-setuid-sandbox'],
  });

  let videoPages = [];

  try {
    const page = await browser.newPage();

    // Intercept window.Reveal assignment before any page scripts run.
    // Reveal.initialize() generates PDF pages; pdfSeparateFragments must be
    // injected here — calling Reveal.configure() after init is too late.
    await page.evaluateOnNewDocument(() => {
      let _reveal = null;
      Object.defineProperty(window, 'Reveal', {
        get() { return _reveal; },
        set(val) {
          if (val && typeof val.initialize === 'function') {
            const origInit = val.initialize.bind(val);
            val.initialize = function (config) {
              return origInit({ ...config, pdfSeparateFragments: false });
            };
          }
          _reveal = val;
        },
        configurable: true,
      });
    });

    // ?print-pdf activates Reveal.js print layout with full-bleed backgrounds
    await page.goto(`file://${src}?print-pdf`, {
      waitUntil: 'networkidle0',
      timeout: 60000,
    });

    // Extra wait for Reveal to finish laying out all slides
    await new Promise((r) => setTimeout(r, 3000));

    // Identify which PDF pages contain videos and replace <video> with poster images + play button
    if (videoFiles.length > 0) {
      const posterDataUrls = {};
      for (const vf of videoFiles) {
        const posterBuf = readFileSync(vf.posterPath);
        posterDataUrls[vf.webmName] = `data:image/png;base64,${posterBuf.toString('base64')}`;
      }

      videoPages = await page.evaluate((posterMap, baseUrl) => {
        const results = [];
        const allPages = [];

        // Collect all printed pages in order
        document.querySelectorAll('.slides .pdf-page').forEach((el) => {
          allPages.push(el);
        });

        // Fallback: Reveal.js nested section counting
        if (allPages.length === 0) {
          document.querySelectorAll('.reveal .slides > section').forEach((section) => {
            const nested = section.querySelectorAll(':scope > section');
            if (nested.length > 0) {
              nested.forEach((s) => allPages.push(s));
            } else {
              allPages.push(section);
            }
          });
        }

        allPages.forEach((pageEl, idx) => {
          const videos = pageEl.querySelectorAll('video');
          videos.forEach((video) => {
            const source = video.querySelector('source');
            const src = source ? source.getAttribute('src') : '';

            if (posterMap[src]) {
              const videoUrl = `${baseUrl}/${src}`;

              // Replace <video> with poster image + play button + URL label
              const img = document.createElement('img');
              img.src = posterMap[src];
              img.style.cssText = video.style.cssText;

              const wrapper = document.createElement('div');
              wrapper.style.cssText = 'position:relative;display:inline-block;';
              wrapper.appendChild(img);

              // Play button overlay
              const playBtn = document.createElement('div');
              playBtn.style.cssText =
                'position:absolute;top:50%;left:50%;transform:translate(-50%,-50%);' +
                'width:80px;height:80px;background:rgba(0,0,0,0.6);border-radius:50%;' +
                'display:flex;align-items:center;justify-content:center;';
              playBtn.innerHTML =
                '<div style="width:0;height:0;border-style:solid;border-width:20px 0 20px 35px;' +
                'border-color:transparent transparent transparent white;margin-left:5px;"></div>';
              wrapper.appendChild(playBtn);

              // URL label below the poster
              const urlLabel = document.createElement('div');
              urlLabel.style.cssText =
                'text-align:center;color:#4fc3f7;font-size:14px;margin-top:8px;' +
                'text-decoration:underline;word-break:break-all;';
              urlLabel.textContent = videoUrl;

              const container = document.createElement('div');
              container.style.cssText = 'display:flex;flex-direction:column;align-items:center;';
              container.appendChild(wrapper);
              container.appendChild(urlLabel);

              video.parentNode.replaceChild(container, video);

              results.push({ pageIndex: idx, webmName: src });
            }
          });
        });

        return results;
      }, posterDataUrls, VIDEO_BASE_URL);
    }

    await page.pdf({
      path: out,
      width: '297mm',
      height: '167.0625mm',
      printBackground: true,
      displayHeaderFooter: false,
      margin: { top: '0', bottom: '0', left: '0', right: '0' },
    });

    console.log(`  → ${out}`);
  } finally {
    await browser.close();
  }

  // Post-process: add clickable Link annotations on video pages
  if (videoPages.length > 0) {
    console.log(`  Adding ${videoPages.length} video link(s) to PDF...`);
    const videoPageEntries = videoPages.map((vp) => {
      const vf = videoFiles.find((f) => f.webmName === vp.webmName);
      return {
        pageIndex: vp.pageIndex,
        webmName: vf.webmName,
        label: vf.label,
      };
    });
    await addVideoLinksInPdf(out, videoPageEntries);
  }

  // Clean up temp files
  try {
    rmSync(tmpDir, { recursive: true, force: true });
  } catch { /* ignore cleanup errors */ }
}

// ── Main ──────────────────────────────────────────────────────────────────────

const target = (process.argv[2] || 'all').toLowerCase();
const valid = ['readme', 'workshop', 'introduction', 'slides', 'all'];

if (!valid.includes(target)) {
  console.error(`Unknown target "${target}". Valid: ${valid.join(', ')}`);
  process.exit(1);
}

const doReadme       = target === 'all' || target === 'readme';
const doWorkshop     = target === 'all' || target === 'workshop';
const doIntroduction = target === 'all' || target === 'introduction';
const doSlides       = target === 'all' || target === 'slides';

if (doReadme) generateReadme();

const needsPuppeteer = doWorkshop || doIntroduction || doSlides;

if (needsPuppeteer) {
  const puppeteer = await loadPuppeteer();

  if (doWorkshop) {
    await generateHtmlPage(puppeteer, {
      htmlFile: 'workshop/index.html',
      outputFile: 'workshop.pdf',
      label: 'workshop',
    });
  }

  if (doIntroduction) {
    await generateRevealDeck(puppeteer, {
      htmlFile: 'introduction/index.html',
      outputFile: 'introduction.pdf',
    });
  }

  if (doSlides) {
    await generateRevealDeck(puppeteer, {
      htmlFile: 'slides/index.html',
      outputFile: 'slides.pdf',
    });
  }
}

console.log('Done.');
