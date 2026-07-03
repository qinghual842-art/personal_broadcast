import request from './request'

export function getAgents() {
  return request.get('/agents')
}

export function getAgent(id) {
  return request.get(`/agents/${id}`)
}

export function chatWithAgent(id, data) {
  return request.post(`/agents/${id}/chat`, data)
}

export function getChatHistory(agentId, sessionId) {
  return request.get(`/agents/${agentId}/chat/${sessionId}`)
}

export function clearChatHistory(agentId, sessionId) {
  return request.delete(`/agents/${agentId}/chat/${sessionId}`)
}

export function getAdminAgents() {
  return request.get('/admin/agents')
}

export function createAgent(data) {
  return request.post('/admin/agents', data)
}

export function getAdminAgent(id) {
  return request.get(`/admin/agents/${id}`)
}

export function updateAgent(id, data) {
  return request.put(`/admin/agents/${id}`, data)
}

export function deleteAgent(id) {
  return request.delete(`/admin/agents/${id}`)
}

export function toggleAgent(id) {
  return request.put(`/admin/agents/${id}/toggle`)
}
