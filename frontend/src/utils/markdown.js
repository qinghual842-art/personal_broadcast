import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

marked.setOptions({
  highlight: function (code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  },
  breaks: true
})

/**
 * Strip leading whitespace from each line so ordinary prose
 * indentation doesn't get misinterpreted as Markdown code blocks.
 * Preserves blank-line paragraph separators.
 */
function normalizeText(content) {
  if (!content) return ''
  return content
    .split('\n')
    .map(line => line.trimStart())
    .join('\n')
}

export function renderMarkdown(content) {
  if (!content) return ''
  return marked(normalizeText(content))
}
