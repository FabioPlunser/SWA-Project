export function handleLogout() {
  localStorage.removeItem('jwt');
  document.cookie = '';
  window.location.href = '/';
}