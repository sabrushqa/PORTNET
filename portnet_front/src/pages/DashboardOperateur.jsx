import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function DashboardOperateur() {
    const [operateur, setOperateur] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchOperateur = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await axios.get('http://localhost:8080/api/auth/me', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setOperateur(response.data);
            } catch (error) {
                console.error("Erreur lors de la récupération de l'opérateur :", error);
            }
        };

        fetchOperateur();
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    if (!operateur) return <p>Chargement des données...</p>;

    return (
        <div style={{ padding: '20px' }}>
            <h2>Bienvenue dans le dashboard opérateur</h2>
            <p><strong>Nom complet :</strong> {operateur.nomComplet}</p>
            <p><strong>Email :</strong> {operateur.email}</p>

            <button
                onClick={handleLogout}
                style={{
                    backgroundColor: '#d9534f',
                    color: 'white',
                    padding: '10px 20px',
                    border: 'none',
                    borderRadius: '5px',
                    cursor: 'pointer',
                    marginTop: '20px'
                }}
            >
                Se déconnecter
            </button>
        </div>
    );
}

export default DashboardOperateur;
