import React, { useState } from 'react';
import axios from 'axios';
import './InscriptionOperateur.css';

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
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await axios.post('http://localhost:8080/api/operateur/register', formData);
            setMessage("✅ Opérateur enregistré avec succès. Vérifiez votre email pour activer votre compte.");
        } catch (error) {
            console.error(error);
            setMessage("❌ Erreur lors de l'inscription. Veuillez réessayer.");
        } finally {
            setIsLoading(false);
        }
    };

    const personalFields = [
        { name: "nomComplet", label: "Nom complet", required: true },
        { name: "email", label: "Email", type: "email", required: true },
        { name: "password", label: "Mot de passe", type: "password", required: true },
    ];

    const companyFields = [
        { name: "societe", label: "Société" },
        { name: "emailProfessionnel", label: "Email professionnel" },
        { name: "telephone", label: "Téléphone" },
        { name: "adresse", label: "Adresse" },
        { name: "ville", label: "Ville" },
        { name: "pays", label: "Pays" }
    ];

    const businessFields = [
        { name: "rc", label: "Registre de Commerce (RC)" },
        { name: "ice", label: "Identifiant Commun de l'Entreprise (ICE)" },
        { name: "ifiscale", label: "Identifiant Fiscal (IF)" },
        { name: "patente", label: "Patente" },
        { name: "domaineActivite", label: "Domaine d'activité" },
        { name: "typeOperation", label: "Type d'opération" },
        { name: "statutDouanier", label: "Statut douanier" }
    ];

    return (
        <div className="registration-container">
            <nav className="registration-nav">
                <div className="nav-content">
                    <div className="nav-logo">
                        <img src="/PortNet.jpg" alt="PortNet Logo" className="nav-logo-image" />
                        <div className="nav-text">
                            <div className="nav-subtitle-french">Guichet Unique National des Procédures du Commerce Extérieur</div>
                            <div className="nav-subtitle-arabic">الشباك الوحيد الوطني لإجراءات التجارة الخارجية</div>
                        </div>
                    </div>

                </div>
            </nav>

            <div className="page-header">

                <p>Rejoignez la plateforme PortNet pour simplifier vos opérations d'import-export</p>
            </div>


            <div className="registration-card">
                <form onSubmit={handleSubmit} className="registration-form">

                    <div className="mega-form-grid">
                        <div className="form-group">
                            <label className="form-label">
                                👤 Nom complet <span className="required">*</span>
                            </label>
                            <input
                                name="nomComplet"
                                value={formData.nomComplet}
                                onChange={handleChange}
                                type="text"
                                required
                                className="form-input"
                                placeholder="Votre nom complet"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">
                                📧 Email <span className="required">*</span>
                            </label>
                            <input
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                type="email"
                                required
                                className="form-input"
                                placeholder="votre@email.com"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">
                                🔒 Mot de passe <span className="required">*</span>
                            </label>
                            <input
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                type="password"
                                required
                                className="form-input"
                                placeholder="Mot de passe sécurisé"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">🏢 Société</label>
                            <input
                                name="societe"
                                value={formData.societe}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Nom de votre société"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">📞 Téléphone</label>
                            <input
                                name="telephone"
                                value={formData.telephone}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="+212 6XX XXX XXX"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">📍 Adresse</label>
                            <input
                                name="adresse"
                                value={formData.adresse}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Adresse complète"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">🏙️ Ville</label>
                            <input
                                name="ville"
                                value={formData.ville}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Casablanca, Rabat..."
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">🌍 Pays</label>
                            <input
                                name="pays"
                                value={formData.pays}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Maroc"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">📋 Registre de Commerce (RC)</label>
                            <input
                                name="rc"
                                value={formData.rc}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Numéro RC"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">🆔 ICE</label>
                            <input
                                name="ice"
                                value={formData.ice}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Identifiant Commun Entreprise"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">💼 Identifiant Fiscal (IF)</label>
                            <input
                                name="ifiscale"
                                value={formData.ifiscale}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Numéro IF"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">📜 Patente</label>
                            <input
                                name="patente"
                                value={formData.patente}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Numéro de patente"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">📧 Email professionnel</label>
                            <input
                                name="emailProfessionnel"
                                value={formData.emailProfessionnel}
                                onChange={handleChange}
                                type="email"
                                className="form-input"
                                placeholder="contact@societe.com"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">🏭 Domaine d'activité</label>
                            <input
                                name="domaineActivite"
                                value={formData.domaineActivite}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Import/Export, Textile..."
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">📦 Type d'opération</label>
                            <input
                                name="typeOperation"
                                value={formData.typeOperation}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Import, Export, Transit..."
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">🛃 Statut douanier</label>
                            <input
                                name="statutDouanier"
                                value={formData.statutDouanier}
                                onChange={handleChange}
                                type="text"
                                className="form-input"
                                placeholder="Statut douanier"
                            />
                        </div>
                    </div>


                    <div className="compact-section">
                        <div className="checkbox-group">
                            <label className="checkbox-label">
                                <input
                                    type="checkbox"
                                    name="certifieISO"
                                    checked={formData.certifieISO}
                                    onChange={handleChange}
                                    className="checkbox-input"
                                />
                                <span className="checkbox-custom"></span>
                                <span className="checkbox-text">
                                    🏆 Certifié ISO (International Organization for Standardization)
                                </span>
                            </label>
                        </div>
                    </div>

                    <div className="form-actions">
                        <button
                            type="submit"
                            className={`submit-button ${isLoading ? 'loading' : ''}`}
                            disabled={isLoading}
                        >
                            {isLoading ? (
                                <>
                                    <span className="spinner"></span>
                                    Inscription en cours...
                                </>
                            ) : (
                                <>
                                    📝 S'inscrire comme Opérateur
                                </>
                            )}
                        </button>
                    </div>
                </form>

                {message && (
                    <div className={`message ${message.includes('✅') ? 'success' : 'error'}`}>
                        {message}
                    </div>
                )}
            </div>
            <div className="registration-footer">
                <p>Déjà inscrit ? <a href="/login">Se connecter</a></p>
                <p>Besoin d'aide ? <a href="/support">Contactez le support</a></p>
            </div>
        </div>
    );
};

export default InscriptionOperateur;