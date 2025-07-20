import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

const authAPI = axios.create({
    baseURL: API_URL,
    headers: { 'Content-Type': 'application/json' },
});

// Fonction login avec gestion d'erreurs
export async function login(email, password) {
    try {
        const response = await authAPI.post('/login', { email, password });
        return response.data;
    } catch (error) {
        if (error.response && error.response.data) {
            // Backend renvoie JSON, on essaie de récupérer un message d'erreur
            const message = error.response.data.message || error.response.data.error || 'Erreur inconnue';
            throw new Error(message);
        } else {
            // Autre erreur (ex: réseau)
            throw new Error(error.message);
        }
    }
}

// Fonction getCurrentUser avec gestion d'erreurs
export async function getCurrentUser(token) {
    try {
        const response = await authAPI.get('/me', {
            headers: { Authorization: `Bearer ${token}` },
        });
        return response.data;
    } catch (error) {
        if (error.response && error.response.data) {
            const message = error.response.data.message || error.response.data.error || 'Erreur inconnue';
            throw new Error(message);
        } else {
            throw new Error(error.message);
        }
    }
}
