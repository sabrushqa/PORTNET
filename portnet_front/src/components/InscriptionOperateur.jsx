import React, { useState } from 'react';
import axios from 'axios';

const InscriptionOperateur = () => {
    const [formData, setFormData] = useState({
        nomComplet: '',
        email: '',
        password: '',
        societe: '',
        telephone: '',
        adresse: '',
        ville: '',
        pays: '',
        rc: '',
        ice: '',
        ifiscale: '',
        patente: '',
        emailProfessionnel: '',
        domaineActivite: '',
        typeOperation: '',
        certifieISO: false,
        statutDouanier: ''
    });

    const [message, setMessage] = useState('');

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post('http://localhost:8080/api/operateur/register', formData);
            setMessage("✅ Opérateur enregistré. Vérifiez votre email pour activer votre compte.");
        } catch (error) {
            console.error(error);
            setMessage("❌ Erreur lors de l'inscription.");
        }
    };

    const fields = [
        { name: "nomComplet", label: "Nom complet", required: true },
        { name: "email", label: "Email", type: "email", required: true },
        { name: "password", label: "Mot de passe", type: "password", required: true },
        { name: "societe", label: "Société" },
        { name: "telephone", label: "Téléphone" },
        { name: "adresse", label: "Adresse" },
        { name: "ville", label: "Ville" },
        { name: "pays", label: "Pays" },
        { name: "rc", label: "RC" },
        { name: "ice", label: "ICE" },
        { name: "ifiscale", label: "IF" },
        { name: "patente", label: "Patente" },
        { name: "emailProfessionnel", label: "Email professionnel" },
        { name: "domaineActivite", label: "Domaine d’activité" },
        { name: "typeOperation", label: "Type d’opération" },
        { name: "statutDouanier", label: "Statut douanier" }
    ];

    return (
        <div style={styles.pageContainer}>
            <h2 style={styles.title}>Inscription Opérateur</h2>

            <form onSubmit={handleSubmit} style={styles.form}>
                <div style={styles.grid}>
                    {fields.map(({ name, label, type = "text", required = false }) => (
                        <input
                            key={name}
                            name={name}
                            value={formData[name]}
                            onChange={handleChange}
                            placeholder={label}
                            type={type}
                            required={required}
                            style={styles.input}
                        />
                    ))}
                </div>

                <label style={styles.checkboxLabel}>
                    <input
                        type="checkbox"
                        name="certifieISO"
                        checked={formData.certifieISO}
                        onChange={handleChange}
                        style={styles.checkbox}
                    />
                    Certifié ISO
                </label>

                <button type="submit" style={styles.button}>
                    ✅ S’inscrire
                </button>
            </form>

            {message && <p style={styles.message}>{message}</p>}
        </div>
    );
};

const styles = {
    pageContainer: {
        width: '100vw',
        minHeight: '100vh',
        padding: '2rem 3rem',
        backgroundColor: '#f0f4f8',
        fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
        boxSizing: 'border-box',
    },
    title: {
        textAlign: 'center',
        color: '#0B3D91', // bleu foncé
        marginBottom: '2rem',
    },
    form: {
        maxWidth: '1200px',
        margin: '0 auto',
        display: 'flex',
        flexDirection: 'column',
        gap: '1rem',
    },
    grid: {
        display: 'flex',
        flexWrap: 'wrap',
        gap: '1rem',
        justifyContent: 'space-between',
    },
    input: {
        flex: '1 1 calc(33% - 1rem)', // 3 par ligne
        minWidth: '220px',
        padding: '12px',
        fontSize: '16px',
        borderRadius: '6px',
        border: '1px solid #ccc',
        boxSizing: 'border-box',
    },
    checkboxLabel: {
        display: 'flex',
        alignItems: 'center',
        gap: '10px',
        color: '#0B3D91',
        fontWeight: '600',
        marginTop: '1rem',
    },
    checkbox: {
        transform: 'scale(1.2)',
        cursor: 'pointer',
    },
    button: {
        marginTop: '1.5rem',
        padding: '14px',
        backgroundColor: '#28a745', // vert
        color: 'white',
        fontWeight: '700',
        fontSize: '18px',
        border: 'none',
        borderRadius: '8px',
        cursor: 'pointer',
        transition: 'background-color 0.3s ease',
    },
    message: {
        marginTop: '1rem',
        textAlign: 'center',
        color: '#0B3D91',
        fontWeight: 'bold',
    }
};

export default InscriptionOperateur;
