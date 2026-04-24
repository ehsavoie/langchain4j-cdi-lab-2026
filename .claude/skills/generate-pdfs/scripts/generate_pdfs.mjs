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
 */

import { execSync } from 'child_process';
import { existsSync } from 'fs';
import { resolve, dirname } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const PROJECT_ROOT = resolve(__dirname, '../../../..');

// Locate puppeteer: try local /tmp install first, then global
async function loadPuppeteer() {
  const localPath = '/tmp/node_modules/puppeteer/lib/esm/puppeteer/puppeteer.js';
  if (existsSync(localPath)) {
    const mod = await import(localPath);
    return mod.default;
  }
  try {
    const mod = await import('puppeteer');
    return mod.default;
  } catch {
    console.error('Puppeteer not found. Install it with: cd /tmp && npm install puppeteer');
    process.exit(1);
  }
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
  console.log(`Generating ${outputFile}...`);

  const browser = await puppeteer.launch({
    headless: true,
    executablePath: '/usr/bin/google-chrome',
    args: ['--no-sandbox', '--disable-setuid-sandbox'],
  });

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

    await page.pdf({
      path: out,
      format: 'A4',
      landscape: true,
      printBackground: true,
      displayHeaderFooter: false,
      margin: { top: '0', bottom: '0', left: '0', right: '0' },
    });

    console.log(`  → ${out}`);
  } finally {
    await browser.close();
  }
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
