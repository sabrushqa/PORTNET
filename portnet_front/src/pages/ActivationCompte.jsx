// src/pages/ActivationCompte.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useSearchParams, useNavigate } from 'react-router-dom';

function ActivationCompte() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const [message, setMessage] = useState('');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = searchParams.get("token");

        const activate = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/auth/activation?token=${token}`);
                setMessage(response.data.message || "Votre compte a été activé avec succès !");
                setError(null);

                // Redirection automatique après 3 secondes
                setTimeout(() => {
                    navigate("/login");
                }, 3000);
            } catch (err) {
                const msg = err.response?.data?.message || "Erreur lors de l'activation du compte.";
                setError(msg);
                setMessage('');
            } finally {
                setLoading(false);
            }
        };

        if (token) {
            activate();
        } else {
            setError("Aucun token fourni.");
            setLoading(false);
        }
    }, [searchParams, navigate]);

    return (
        <div style={{ maxWidth: 500, margin: 'auto', padding: 20, textAlign: 'center' }}>
            <h2>Activation du compte</h2>
            {loading && <p>⏳ Activation en cours...</p>}
            {!loading && message && <p style={{ color: 'green' }}>{message}</p>}
            {!loading && error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
}

export default ActivationCompte;
