import html2canvas from 'html2canvas'
import { jsPDF } from 'jspdf'

const A4_WIDTH_PX = 794
const A4_HEIGHT_PX = Math.round(794 * 1.414) // 1123
const SCALE = 2

async function toDataUrl(url) {
  try {
    const resp = await fetch(url, { mode: 'cors' })
    const blob = await resp.blob()
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onloadend = () => resolve(reader.result)
      reader.onerror = reject
      reader.readAsDataURL(blob)
    })
  } catch {
    return null
  }
}

async function inlineImages(element) {
  const imgs = element.querySelectorAll('img')
  let allInlined = true
  for (const img of imgs) {
    if (!img.src || img.src.startsWith('data:')) continue
    const dataUrl = await toDataUrl(img.src)
    if (dataUrl) {
      img.src = dataUrl
    } else {
      allInlined = false
    }
  }
  const promises = Array.from(imgs).map(img => {
    if (img.complete && img.naturalWidth > 0) return Promise.resolve()
    return new Promise(resolve => {
      img.onload = resolve
      img.onerror = resolve
    })
  })
  await Promise.all(promises)
  return allInlined
}

async function buildPdf(paperElements) {
  const elements = Array.isArray(paperElements) ? paperElements : [paperElements]

  const pdf = new jsPDF({
    orientation: 'portrait',
    unit: 'mm',
    format: 'a4',
  })

  let isFirstPage = true

  for (const paperElement of elements) {
    const container = document.createElement('div')
    container.style.cssText = `
      position: fixed;
      left: -9999px;
      top: 0;
      width: ${A4_WIDTH_PX}px;
      z-index: -1;
      background: white;
      overflow: visible;
    `
    const clone = paperElement.cloneNode(true)
    clone.style.transform = 'none'
    clone.style.width = `${A4_WIDTH_PX}px`
    clone.style.minHeight = `${A4_HEIGHT_PX}px`
    clone.style.margin = '0'
    container.appendChild(clone)
    document.body.appendChild(container)

    try {
      const allInlined = await inlineImages(clone)

      const canvas = await html2canvas(clone, {
        scale: SCALE,
        useCORS: true,
        allowTaint: !allInlined,
        logging: false,
        width: A4_WIDTH_PX,
        windowWidth: A4_WIDTH_PX,
        backgroundColor: '#ffffff',
      })

      const pageHeightPx = A4_HEIGHT_PX * SCALE
      const canvasWidth = canvas.width
      const canvasHeight = canvas.height
      const totalPages = Math.ceil(canvasHeight / pageHeightPx)

      for (let i = 0; i < totalPages; i++) {
        if (!isFirstPage) pdf.addPage()
        isFirstPage = false

        const sliceHeight = Math.min(pageHeightPx, canvasHeight - i * pageHeightPx)
        const pageCanvas = document.createElement('canvas')
        pageCanvas.width = canvasWidth
        pageCanvas.height = sliceHeight

        const ctx = pageCanvas.getContext('2d')
        ctx.fillStyle = '#ffffff'
        ctx.fillRect(0, 0, canvasWidth, sliceHeight)
        ctx.drawImage(
          canvas,
          0, i * pageHeightPx,
          canvasWidth, sliceHeight,
          0, 0,
          canvasWidth, sliceHeight
        )

        const imgData = pageCanvas.toDataURL('image/png')
        const imgWidthMm = 210
        const imgHeightMm = (sliceHeight / SCALE) * (210 / A4_WIDTH_PX)
        pdf.addImage(imgData, 'PNG', 0, 0, imgWidthMm, imgHeightMm)
      }
    } finally {
      document.body.removeChild(container)
    }
  }

  return pdf
}

export async function exportResumePdf(paperElements, filename = '简历') {
  const pdf = await buildPdf(paperElements)
  pdf.save(`${filename}.pdf`)
}

export async function exportResumePdfAsBlob(paperElements) {
  const pdf = await buildPdf(paperElements)
  return pdf.output('blob')
}
