export function handleLogout() {
  localStorage.removeItem('token');
  window.location.href = '/';
}