import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useSearchParams, useNavigate } from 'react-router-dom';
import './ActivateOperateur.css';

function ActivateOperateur() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const [status, setStatus] = useState('loading'); // loading, success, error, already_activated
    const [message, setMessage] = useState('');
    const [countdown, setCountdown] = useState(360); // 6 minutes en secondes

    useEffect(() => {
        const token = searchParams.get("token");

        const activate = async () => {
            if (!token) {
                setStatus('error');
                setMessage('Lien d\'activation invalide. Aucun token fourni.');
                return;
            }

            try {
                console.log("Tentative d'activation avec token:", token);

                const response = await axios.get(
                    `http://localhost:8080/api/auth/activation?token=${token}`,
                    {
                        timeout: 10000,
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }
                );

                console.log("Activation réussie:", response.data);
                setStatus('success');
                setMessage(response.data.message || "Votre compte a été activé avec succès !");

                // Démarrer le countdown
                startCountdown();

            } catch (err) {
                console.error("Erreur d'activation:", err);

                if (err.response?.status === 400) {
                    const errorMsg = err.response.data?.message || '';

                    if (errorMsg.includes('Token invalide') || errorMsg.includes('Token non trouvé')) {
                        setStatus('already_activated');
                        setMessage('Ce compte est peut-être déjà activé. Vous allez être redirigé vers la page de connexion.');
                        startCountdown();
                    } else if (errorMsg.includes('Token expiré')) {
                        setStatus('error');
                        setMessage('Le lien d\'activation a expiré. Veuillez demander un nouveau lien d\'activation.');
                    } else {
                        setStatus('error');
                        setMessage(errorMsg || 'Erreur lors de l\'activation du compte.');
                    }
                } else if (err.code === 'ECONNABORTED') {
                    setStatus('error');
                    setMessage('Délai d\'attente dépassé. Veuillez réessayer.');
                } else if (err.response?.status === 403) {
                    setStatus('error');
                    setMessage('Accès refusé. Veuillez contacter le support.');
                } else {
                    setStatus('error');
                    setMessage('Erreur de connexion au serveur. Veuillez réessayer plus tard.');
                }
            }
        };

        activate();
    }, [searchParams]);

    const startCountdown = () => {
        const timer = setInterval(() => {
            setCountdown(prev => {
                if (prev <= 1) {
                    clearInterval(timer);
                    navigate("/login");
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
    };

    const handleGoToLogin = () => {
        navigate("/login");
    };

    const handleRequestNewLink = () => {
        navigate("/forgot-password");
    };

    return (
        <div className="activation-container">
            <div className="activation-card">
                <div className="activation-header">
                    <div className="logo">
                        <img src="/PortNet.jpg" alt="PortNet Logo" className="logo-image" />
                    </div>
                    <div className="subtitle-arabic">
                        الشباك الوحيد الوطني لإجراءات التجارة الخارجية
                    </div>
                    <div className="subtitle-french">
                        Guichet Unique National des Procédures du Commerce Extérieur
                    </div>
                    <h2>Activation du compte</h2>
                    <h3>📦 <strong>IMPORTATEUR - EXPORTATEUR</strong></h3>
                </div>

                <div className="activation-content">
                    {status === 'loading' && (
                        <div className="status-section loading">
                            <div className="spinner"></div>
                            <h3>Activation en cours...</h3>
                            <p>Veuillez patienter pendant que nous activons votre compte.</p>
                        </div>
                    )}

                    {status === 'success' && (
                        <div className="status-section success">
                            <div className="icon success-icon">
                                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M9 12L11 14L15 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
                                </svg>
                            </div>
                            <h3>Compte activé avec succès !</h3>
                            <p>{message}</p>
                            <div className="countdown">
                                <p>Redirection automatique dans <span className="countdown-number">{Math.floor(countdown / 60)}:{(countdown % 60).toString().padStart(2, '0')}</span></p>
                            </div>
                            <button onClick={handleGoToLogin} className="btn btn-primary">
                                Se connecter maintenant
                            </button>
                        </div>
                    )}

                    {status === 'already_activated' && (
                        <div className="status-section info">
                            <div className="icon info-icon">
                                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
                                    <path d="M12 16V12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    <path d="M12 8H12.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                </svg>
                            </div>
                            <h3>Compte déjà activé</h3>
                            <p>{message}</p>
                            <div className="countdown">
                                <p>Redirection automatique dans <span className="countdown-number">{Math.floor(countdown / 60)}:{(countdown % 60).toString().padStart(2, '0')}</span></p>
                            </div>
                            <button onClick={handleGoToLogin} className="btn btn-primary">
                                Se connecter maintenant
                            </button>
                        </div>
                    )}

                    {status === 'error' && (
                        <div className="status-section error">
                            <div className="icon error-icon">
                                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
                                    <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" strokeWidth="2"/>
                                    <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" strokeWidth="2"/>
                                </svg>
                            </div>
                            <h3>Erreur d'activation</h3>
                            <p>{message}</p>
                            <div className="button-group">
                                <button onClick={handleGoToLogin} className="btn btn-secondary">
                                    Aller à la connexion
                                </button>
                                <button onClick={handleRequestNewLink} className="btn btn-primary">
                                    Demander un nouveau lien
                                </button>
                            </div>
                        </div>
                    )}
                </div>

                <div className="activation-footer">
                    <p>Besoin d'aide ? <a href="/support">Contactez le support</a></p>
                </div>
            </div>
        </div>
    );
}

export default ActivateOperateur;