import React, { useEffect, useState } from 'react';
import axios from 'axios';

function SuperviseurAgents() {
    const [agents, setAgents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const token = localStorage.getItem('token');

    useEffect(() => {
        axios.get('http://localhost:8080/api/agents', {
            headers: { Authorization: `Bearer ${token}` }
        }).then(res => {
            setAgents(res.data);
            setLoading(false);
        }).catch(err => {
            setError('Erreur lors de la récupération des agents');
            setLoading(false);
        });
    }, []);

    const toggleActivation = async (agentId) => {
        try {
            const res = await axios.post(`http://localhost:8080/api/agents/${agentId}/toggle-activation`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            // Mettre à jour l'état local
            setAgents(agents.map(a =>
                a.id === agentId ? { ...a, enabled: res.data.enabled } : a
            ));
        } catch {
            alert("Erreur lors du changement de statut");
        }
    };

    if (loading) return <p>Chargement...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div style={{ padding: '2rem' }}>
            <h2>Liste des Agents</h2>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead style={{ backgroundColor: '#0B3D91', color: 'white' }}>
                <tr>
                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>Nom complet</th>
                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>Email</th>
                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>Dernière connexion</th>
                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>État</th>
                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>Actions</th>
                </tr>
                </thead>
                <tbody>
                {agents.map(agent => (
                    <tr key={agent.id} style={{ backgroundColor: agent.enabled ? 'white' : '#f8d7da' }}>
                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{agent.nomComplet}</td>
                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{agent.email}</td>
                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                            {agent.lastLogin ? new Date(agent.lastLogin).toLocaleString() : 'Jamais connecté'}
                        </td>
                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                            {agent.enabled ? 'Activé' : 'Désactivé'}
                        </td>
                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                            <button
                                onClick={() => toggleActivation(agent.id)}
                                style={{
                                    backgroundColor: agent.enabled ? '#dc3545' : '#28a745',
                                    color: 'white',
                                    border: 'none',
                                    padding: '6px 12px',
                                    borderRadius: '4px',
                                    cursor: 'pointer',
                                }}
                            >
                                {agent.enabled ? 'Désactiver' : 'Activer'}
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default SuperviseurAgents;
