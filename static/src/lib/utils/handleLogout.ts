export function handleLogout() {
  localStorage.removeItem('token');
  document.cookie = '';
  window.location.href = '/';
}