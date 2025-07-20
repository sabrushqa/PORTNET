import { useState } from "react";
import axios from "axios";

export default function AddAgentForm() {
    const [formData, setFormData] = useState({
        nomComplet: "",
        email: "",
        telephone: "",
        departement: ""
    });

    const [message, setMessage] = useState("");

    const handleChange = (e) => {
        setFormData((prev) => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post("http://localhost:8080/api/agents", formData, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`
                }
            });
            setMessage("Agent ajouté avec succès. Email envoyé !");
            setFormData({ nomComplet: "", email: "", telephone: "", departement: "" });
        } catch (err) {
            setMessage("Erreur : " + (err.response?.data || "Impossible d'ajouter l'agent."));
        }
    };

    return (
        <div>
            <h2>Ajouter un agent</h2>
            <form onSubmit={handleSubmit}>
                <input name="nomComplet" placeholder="Nom complet" value={formData.nomComplet} onChange={handleChange} required />
                <input name="email" type="email" placeholder="Email" value={formData.email} onChange={handleChange} required />
                <input name="telephone" placeholder="Téléphone" value={formData.telephone} onChange={handleChange} />
                <input name="departement" placeholder="Département" value={formData.departement} onChange={handleChange} />

                <button type="submit">Créer</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
}
