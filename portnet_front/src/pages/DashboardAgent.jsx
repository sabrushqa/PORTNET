import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const DashboardAgent = () => {
    const [agent, setAgent] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchAgent = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await axios.get('http://localhost:8080/api/auth/me', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setAgent(response.data);
            } catch (error) {
                console.error("Erreur lors de la récupération de l’agent :", error);
                navigate('/login');
            }
        };

        fetchAgent();
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    if (!agent) {
        return <p>Chargement...</p>;
    }

    return (
        <div style={{ padding: '20px' }}>
            <h1>Bonjour Mr {agent.nomComplet}</h1>
            <p><strong>Prénom et Nom :</strong> {agent.nomComplet}</p>
            <p><strong>Email :</strong> {agent.email}</p>
            <p><strong>Bienvenue dans votre espace agent.</strong></p>

            <button
                onClick={handleLogout}
                style={{
                    backgroundColor: '#dc3545',
                    color: 'white',
                    border: 'none',
                    borderRadius: '5px',
                    padding: '10px 20px',
                    cursor: 'pointer',
                    fontWeight: 'bold',
                    marginTop: '20px'
                }}
            >
                🔓 Se déconnecter
            </button>
        </div>
    );
};

export default DashboardAgent;
