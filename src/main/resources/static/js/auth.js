const API = '/api';
const TOKEN_KEY = 'token';
const USER_KEY = 'user';

function storage(remember) {
  return remember ? localStorage : sessionStorage;
}

function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(USER_KEY);
}

function saveAuth(token, user, remember) {
  clearAuth();
  const store = storage(remember);
  store.setItem(TOKEN_KEY, token);
  store.setItem(USER_KEY, JSON.stringify(user));
  if (remember) {
    localStorage.setItem('rememberMe', '1');
  } else {
    localStorage.removeItem('rememberMe');
  }
}

// Redirect if already logged in
(function () {
  const token = localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY);
  if (token && !window.location.pathname.includes('app')) {
    // stay on login if explicitly signing out; otherwise optional auto-redirect
  }
})();

const loginForm = document.getElementById('loginForm');
const panelLogin = document.getElementById('panelLogin');
const panelForgot = document.getElementById('panelForgot');

document.getElementById('btnShowForgot')?.addEventListener('click', () => {
  panelLogin?.classList.add('d-none');
  panelForgot?.classList.remove('d-none');
});
document.getElementById('btnBackLogin')?.addEventListener('click', () => {
  panelForgot?.classList.add('d-none');
  panelLogin?.classList.remove('d-none');
});

document.getElementById('btnTogglePwd')?.addEventListener('click', () => {
  const input = document.getElementById('password');
  const icon = document.querySelector('#btnTogglePwd i');
  if (!input) return;
  const show = input.type === 'password';
  input.type = show ? 'text' : 'password';
  if (icon) icon.className = show ? 'bi bi-eye-slash' : 'bi bi-eye';
});

loginForm?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;
  const rememberMe = document.getElementById('rememberMe')?.checked === true;
  const errEl = document.getElementById('loginError');
  const btn = document.getElementById('loginBtn');
  const text = btn.querySelector('.btn-text');
  const spin = btn.querySelector('.spinner-border');
  errEl.classList.add('d-none');
  btn.disabled = true;
  text?.classList.add('d-none');
  spin?.classList.remove('d-none');
  try {
    const res = await fetch(`${API}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password, rememberMe })
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.message || data.error || 'Sign in failed');
    const token = data.token || data.accessToken;
    if (!token) throw new Error('No token returned');
    saveAuth(token, {
      id: data.userId,
      fullName: data.fullName,
      email: data.email,
      role: data.role
    }, rememberMe);
    window.location.href = '/app.html';
  } catch (err) {
    errEl.textContent = err.message || 'Sign in failed';
    errEl.classList.remove('d-none');
  } finally {
    btn.disabled = false;
    text?.classList.remove('d-none');
    spin?.classList.add('d-none');
  }
});

document.getElementById('forgotForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const email = document.getElementById('forgotEmail').value.trim();
  const msg = document.getElementById('forgotMsg');
  const btn = document.getElementById('forgotBtn');
  const text = btn.querySelector('.btn-text');
  const spin = btn.querySelector('.spinner-border');
  msg.classList.add('d-none');
  btn.disabled = true;
  text?.classList.add('d-none');
  spin?.classList.remove('d-none');
  try {
    const res = await fetch(`${API}/auth/forgot-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.message || 'Request failed');
    msg.className = 'alert alert-success py-2 small';
    msg.textContent = data.message || 'If that email is registered, a reset message was sent. Check your inbox (and spam).';
    msg.classList.remove('d-none');
  } catch (err) {
    msg.className = 'alert alert-danger py-2 small';
    msg.textContent = err.message;
    msg.classList.remove('d-none');
  } finally {
    btn.disabled = false;
    text?.classList.remove('d-none');
    spin?.classList.add('d-none');
  }
});

document.getElementById('resetForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const token = document.getElementById('resetToken').value.trim();
  const newPassword = document.getElementById('resetPassword').value;
  const msg = document.getElementById('resetMsg');
  msg.classList.add('d-none');
  try {
    const res = await fetch(`${API}/auth/reset-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token, newPassword })
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.message || 'Reset failed');
    msg.className = 'alert alert-success py-2 small';
    msg.textContent = data.message || 'Password reset successful. You can sign in now.';
    msg.classList.remove('d-none');
  } catch (err) {
    msg.className = 'alert alert-danger py-2 small';
    msg.textContent = err.message;
    msg.classList.remove('d-none');
  }
});


// Deep link: /index.html?otp=123456 opens reset panel with OTP filled
(function openResetFromQuery() {
  const params = new URLSearchParams(window.location.search);
  const otp = params.get('otp') || params.get('token');
  if (!otp) return;
  document.getElementById('panelLogin')?.classList.add('d-none');
  document.getElementById('panelForgot')?.classList.remove('d-none');
  const tokenInput = document.getElementById('resetToken');
  if (tokenInput) tokenInput.value = otp;
})();
