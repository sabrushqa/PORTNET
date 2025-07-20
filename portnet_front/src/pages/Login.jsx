import React, { useState } from 'react';
import { Eye, EyeOff, Mail, Lock, AlertCircle, CheckCircle } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';

const Login = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [alert, setAlert] = useState({ type: '', message: '', visible: false });

    const showAlert = (type, message) => {
        setAlert({ type, message, visible: true });
        setTimeout(() => {
            setAlert({ type: '', message: '', visible: false });
        }, 5000);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (response.ok) {
                showAlert('success', 'Connexion réussie ! Redirection en cours...');

                // Stockage du token et rôle utilisateur
                const token = data.token;
                const userRole = data.role;

                localStorage.setItem('token', token);
                localStorage.setItem('role', userRole);


                if (userRole === 'OPERATEUR') {
                    navigate('/dashboard-operateur');
                } else if (userRole === 'SUPERVISEUR') {
                    navigate('/dashboard');
                } else if (userRole === 'AGENT') {
                    navigate('/dashboard-agent');
                } else {
                    navigate('/dashboard1'); // fallback
                }


            } else {
                showAlert('error', data.message || 'Identifiants invalides');
            }
        } catch (error) {
            showAlert('error', 'Erreur de connexion. Veuillez réessayer.');
        } finally {
            setLoading(false);
        }
    };


    const styles = {
        container: {
            minHeight: '100vh',
            background: 'linear-gradient(135deg, #0f172a 0%, #1e3a8a 50%, #0f172a 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
            fontFamily: 'system-ui, -apple-system, sans-serif'
        },
        card: {
            maxWidth: '450px',
            width: '100%',
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderRadius: '20px',
            padding: '40px',
            boxShadow: '0 20px 40px rgba(0, 0, 0, 0.1)',
            border: '1px solid rgba(255, 255, 255, 0.3)'
        },
        logoSection: {
            textAlign: 'center',
            marginBottom: '30px'
        },
        logoContainer: {
            marginBottom: '20px'
        },
        logo: {
            height: '100px',
            width: 'auto',
            maxWidth: '100%',
            objectFit: 'contain'
        },
        logoFallback: {
            display: 'none'
        },
        title: {
            fontSize: '28px',
            fontWeight: 'bold',
            color: '#1e3a8a',
            margin: '15px 0 10px 0'
        },
        subtitle: {
            color: '#64748b',
            fontSize: '14px',
            marginBottom: '8px',
            fontWeight: '500'
        },
        subtitleEn: {
            color: '#64748b',
            fontSize: '12px',
            marginBottom: '0'
        },
        formGroup: {
            marginBottom: '20px'
        },
        label: {
            display: 'block',
            fontSize: '14px',
            fontWeight: '600',
            color: '#374151',
            marginBottom: '8px'
        },
        inputContainer: {
            position: 'relative'
        },
        inputIcon: {
            position: 'absolute',
            left: '15px',
            top: '50%',
            transform: 'translateY(-50%)',
            color: '#6b7280',
            pointerEvents: 'none'
        },
        input: {
            width: '100%',
            padding: '12px 15px 12px 45px',
            border: '2px solid #e5e7eb',
            borderRadius: '10px',
            fontSize: '16px',
            outline: 'none',
            transition: 'all 0.3s ease',
            boxSizing: 'border-box',
            backgroundColor: '#ffffff'
        },
        passwordInput: {
            width: '100%',
            padding: '12px 45px 12px 45px',
            border: '2px solid #e5e7eb',
            borderRadius: '10px',
            fontSize: '16px',
            outline: 'none',
            transition: 'all 0.3s ease',
            boxSizing: 'border-box',
            backgroundColor: '#ffffff'
        },
        passwordToggle: {
            position: 'absolute',
            right: '15px',
            top: '50%',
            transform: 'translateY(-50%)',
            background: 'none',
            border: 'none',
            color: '#6b7280',
            cursor: 'pointer',
            padding: '5px'
        },
        submitButton: {
            width: '100%',
            padding: '15px',
            background: 'linear-gradient(135deg, #059669, #10b981)',
            color: 'white',
            border: 'none',
            borderRadius: '10px',
            fontSize: '16px',
            fontWeight: '600',
            cursor: 'pointer',
            transition: 'all 0.3s ease',
            marginBottom: '20px'
        },
        submitButtonDisabled: {
            opacity: '0.7',
            cursor: 'not-allowed'
        },
        spinner: {
            display: 'inline-block',
            width: '20px',
            height: '20px',
            border: '2px solid rgba(255, 255, 255, 0.3)',
            borderTop: '2px solid white',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite',
            marginRight: '10px'
        },
        forgotPassword: {
            textAlign: 'center',
            marginBottom: '20px'
        },
        forgotPasswordLink: {
            color: '#1e3a8a',
            textDecoration: 'none',
            fontSize: '14px',
            fontWeight: '500'
        },
        footer: {
            textAlign: 'center',
            color: '#6b7280',
            fontSize: '12px',
            marginTop: '20px',
            paddingTop: '20px',
            borderTop: '1px solid #e5e7eb'
        },
        alert: {
            position: 'fixed',
            top: '20px',
            right: '20px',
            maxWidth: '400px',
            width: '90%',
            backgroundColor: 'white',
            borderRadius: '10px',
            boxShadow: '0 10px 25px rgba(0, 0, 0, 0.15)',
            padding: '16px',
            zIndex: 1000,
            display: 'flex',
            alignItems: 'center',
            gap: '12px'
        },
        alertSuccess: {
            borderLeft: '4px solid #10b981'
        },
        alertError: {
            borderLeft: '4px solid #ef4444'
        },
        alertText: {
            fontSize: '14px',
            fontWeight: '500',
            margin: 0
        },
        alertTextSuccess: {
            color: '#065f46'
        },
        alertTextError: {
            color: '#991b1b'
        }
    };

    const Alert = ({ type, message, visible }) => {
        if (!visible) return null;

        const alertStyle = {
            ...styles.alert,
            ...(type === 'success' ? styles.alertSuccess : styles.alertError)
        };

        const textStyle = {
            ...styles.alertText,
            ...(type === 'success' ? styles.alertTextSuccess : styles.alertTextError)
        };

        return (
            <div style={alertStyle}>
                {type === 'success' ? (
                    <CheckCircle size={20} color="#10b981" />
                ) : (
                    <AlertCircle size={20} color="#ef4444" />
                )}
                <p style={textStyle}>{message}</p>
            </div>
        );
    };

    return (
        <>
            <style>{`
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
                
                input:focus {
                    border-color: #1e3a8a !important;
                    box-shadow: 0 0 0 3px rgba(30, 58, 138, 0.1) !important;
                }
                
                button:hover:not(:disabled) {
                    background: linear-gradient(135deg, #047857, #059669) !important;
                    transform: translateY(-2px) !important;
                    box-shadow: 0 8px 25px rgba(16, 185, 129, 0.3) !important;
                }
                
                a:hover {
                    color: #1e40af !important;
                }
                
                body {
                    margin: 0;
                    padding: 0;
                    font-family: system-ui, -apple-system, sans-serif;
                }
                
                input::placeholder {
                    color: #9ca3af;
                }
            `}</style>

            <div style={styles.container}>
                <Alert {...alert} />

                <div style={styles.card}>
                    {/* Logo Section */}
                    <div style={styles.logoSection}>
                        <div style={styles.logoContainer}>
                            <img
                                src="/PortNet.jpg"
                                alt="PORTNET Logo"
                                style={styles.logo}
                                onError={(e) => {
                                    e.target.style.display = 'none';
                                    const fallback = e.target.nextSibling;
                                    if (fallback) {
                                        fallback.style.display = 'block';
                                    }
                                }}
                            />
                            <div style={{...styles.logoFallback, textAlign: 'center'}}>
                                <h1 style={styles.title}>PORTNET</h1>
                            </div>
                        </div>
                        <p style={styles.subtitle}>
                            الشباك الموحد الوطني لمساطر التجارة الخارجية
                        </p>
                        <p style={styles.subtitleEn}>
                            GUICHET UNIQUE NATIONAL DES PROCÉDURES DU COMMERCE EXTÉRIEUR
                        </p>
                    </div>

                    {/* Email Field */}
                    <div style={styles.formGroup}>
                        <label style={styles.label}>Adresse email</label>
                        <div style={styles.inputContainer}>
                            <div style={styles.inputIcon}>
                                <Mail size={20} />
                            </div>
                            <input
                                type="email"
                                required
                                style={styles.input}
                                placeholder="votre@email.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                    </div>

                    {/* Password Field */}
                    <div style={styles.formGroup}>
                        <label style={styles.label}>Mot de passe</label>
                        <div style={styles.inputContainer}>
                            <div style={styles.inputIcon}>
                                <Lock size={20} />
                            </div>
                            <input
                                type={showPassword ? 'text' : 'password'}
                                required
                                style={styles.passwordInput}
                                placeholder="••••••••"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                            <button
                                type="button"
                                style={styles.passwordToggle}
                                onClick={() => setShowPassword(!showPassword)}
                            >
                                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                            </button>
                        </div>
                    </div>

                    {/* Submit Button */}
                    <button
                        onClick={handleSubmit}
                        disabled={loading}
                        style={{
                            ...styles.submitButton,
                            ...(loading ? styles.submitButtonDisabled : {})
                        }}
                    >
                        {loading ? (
                            <>
                                <span style={styles.spinner}></span>
                                Connexion en cours...
                            </>
                        ) : (
                            'Se connecter'
                        )}
                    </button>

                    {/* Forgot Password */}
                    <div style={styles.forgotPassword}>
                        <a href="#" style={styles.forgotPasswordLink}>
                            Mot de passe oublié ?
                        </a>
                        <Link to="/inscription-operateur">Inscription Opérateur</Link>
                    </div>

                    {/* Footer */}
                    <div style={styles.footer}>
                        © 2025 PORTNET. Tous droits réservés.
                    </div>
                </div>
            </div>
        </>
    );
};

export default Login;