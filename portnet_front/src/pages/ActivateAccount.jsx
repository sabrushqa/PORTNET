import React, { useState } from "react";
import { useSearchParams } from "react-router-dom";
import axios from "axios";

export default function AgentActivation() {
    const [password, setPassword] = useState("");
    const [confirm, setConfirm] = useState("");
    const [message, setMessage] = useState("");
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");

    const handleActivate = async (e) => {
        e.preventDefault();

        if (password !== confirm) {
            setMessage("Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            const response = await axios.post("http://localhost:8080/api/agents/activation", {
                token,
                password,
            });
            setMessage(response.data);
        } catch (error) {
            console.error(error);
            if (error.response && error.response.status === 400) {
                setMessage("Token invalide ou expiré.");
            } else {
                setMessage("Erreur serveur lors de l’activation.");
            }
        }
    };

    return (
        <div>
            <h2>Activer votre compte</h2>
            <form onSubmit={handleActivate}>
                <input
                    type="password"
                    placeholder="Nouveau mot de passe"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="Confirmer le mot de passe"
                    value={confirm}
                    onChange={(e) => setConfirm(e.target.value)}
                    required
                />
                <button type="submit">Activer</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
}
