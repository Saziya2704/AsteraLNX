function getToken() {
  return sessionStorage.getItem('token') || localStorage.getItem('token');
}
function getUser() {
  try {
    const raw = sessionStorage.getItem('user') || localStorage.getItem('user') || '{}';
    return JSON.parse(raw);
  } catch { return {}; }
}
function clearAuthStorage() {
  ['token', 'user'].forEach(k => {
    localStorage.removeItem(k);
    sessionStorage.removeItem(k);
  });
  localStorage.removeItem('rememberMe');
}

async function api(path, options = {}) {
  const headers = { ...(options.headers || {}) };
  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = headers['Content-Type'] || 'application/json';
  }
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;
  const res = await fetch('/api' + path, { ...options, headers });
  if (res.status === 401) {
    clearAuthStorage();
    window.location.href = '/index.html';
    throw new Error('Session expired');
  }
  return res;
}

async function apiGet(path) {
  const res = await api(path, { method: 'GET' });
  if (res.status === 204) return null;
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || data.error || ('Request failed (' + res.status + ')'));
  return data;
}

async function apiPost(path, body) {
  const res = await api(path, {
    method: 'POST',
    body: body === undefined ? undefined : JSON.stringify(body)
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || data.error || ('Request failed (' + res.status + ')'));
  return data;
}

async function apiPut(path, body) {
  const res = await api(path, {
    method: 'PUT',
    body: body === undefined ? undefined : JSON.stringify(body)
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || data.error || ('Request failed (' + res.status + ')'));
  return data;
}

async function apiPatch(path, body) {
  const res = await api(path, {
    method: 'PATCH',
    body: body === undefined ? undefined : JSON.stringify(body)
  });
  if (res.status === 204) return null;
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || data.error || ('Request failed (' + res.status + ')'));
  return data;
}

async function apiDelete(path) {
  const res = await api(path, { method: 'DELETE' });
  if (res.status === 204) return null;
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || data.error || ('Request failed (' + res.status + ')'));
  return data;
}

function toast(message, type = 'success') {
  let host = document.getElementById('toastHost');
  if (!host) {
    host = document.createElement('div');
    host.id = 'toastHost';
    host.className = 'toast-host';
    document.body.appendChild(host);
  }
  const el = document.createElement('div');
  el.className = `app-toast app-toast-${type} animate-toast-in`;
  el.innerHTML = `<i class="bi ${type === 'success' ? 'bi-check-circle-fill' : type === 'error' ? 'bi-x-circle-fill' : 'bi-info-circle-fill'}"></i><span>${message}</span>`;
  host.appendChild(el);
  setTimeout(() => {
    el.classList.add('animate-toast-out');
    setTimeout(() => el.remove(), 280);
  }, 3200);
}
