---
name: generate-pdfs
description: Generate PDF exports for the langchain4j-cdi-lab-2026 presentation project. Use this skill whenever the user asks to produce, export, or generate PDFs from any of the project documents: README.md, workshop/index.html, introduction/index.html, or slides/index.html. Accepts arguments: readme, workshop, introduction, slides, or all (default).
---

# generate-pdfs

Generates PDF exports for all project documents using the correct technique for each file type.

## Usage

Run the bundled script from the project root:

```bash
node <skill_dir>/scripts/generate_pdfs.mjs [readme|workshop|introduction|slides|all]
```

- **No argument / `all`**: generates all four PDFs
- **`readme`**: README.md → README.pdf
- **`workshop`**: workshop/index.html → workshop.pdf
- **`introduction`**: introduction/index.html → introduction.pdf
- **`slides`**: slides/index.html → slides.pdf

Output files land in the project root directory.

## Prerequisites

**Chrome** must be installed (used by both `md-to-pdf` and Puppeteer):
- Expected at `/usr/bin/google-chrome` (already present on this machine)

**Puppeteer** must be installed at `/tmp`. Install once if missing:

```bash
cd /tmp && PUPPETEER_SKIP_DOWNLOAD=true npm install puppeteer
```

`PUPPETEER_SKIP_DOWNLOAD=true` skips downloading a bundled Chrome (we use the system one).
The script auto-detects puppeteer at `/tmp/node_modules/puppeteer` and falls back
to trying a global import.

## Techniques — why each approach is needed

### README → PDF (`npx md-to-pdf`)

Simple Markdown-to-PDF via `md-to-pdf`. Uses the system Chrome (passed via `--launch-options`).
The output PDF is written to the same directory as the source file (project root for README.md).

### Workshop / Introduction HTML → PDF (Puppeteer)

These are regular HTML pages with a sticky navigation header.

**Problem 1 — navigation header repeats on every page.**
The header uses `position: fixed`, which the browser repeats on every print page.
**Fix**: inject a print-media CSS rule that overrides `position: fixed` → `position: relative`
so the header flows naturally at the top of the first page only.

**Problem 2 — browser prints URL/date in header and footer.**
Puppeteer's `page.pdf()` option `displayHeaderFooter: true` with an empty `headerTemplate`
and a custom `footerTemplate` suppresses the browser's default header while still
allowing a page-number footer.

### Slides → PDF (Puppeteer + Reveal.js interception)

Reveal.js generates one PDF page per animation fragment by default, causing 3–6×
page duplication (e.g. 153 pages instead of 52).

**Problem**: `pdfSeparateFragments: false` must be set *before* `Reveal.initialize()`
is called. Calling `Reveal.configure()` after init has no effect on PDF page generation.

**Fix**: `page.evaluateOnNewDocument()` intercepts the `window.Reveal` property
assignment (via `Object.defineProperty`) before any scripts run, wrapping `initialize()`
to inject `pdfSeparateFragments: false` into whatever config the page passes.

The slides are loaded with the `?print-pdf` query string, which activates Reveal.js's
print layout mode.
