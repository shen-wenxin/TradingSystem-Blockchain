export function getLocalToken() {
    return localStorage.getItem('token');
}

export function setLocalToken(token) {
    localStorage.setItem('token', token)
}

export function removeLocalToken() {
    localStorage.removeItem('token');
}