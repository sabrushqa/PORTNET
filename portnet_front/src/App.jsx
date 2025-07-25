import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import InscriptionOperateur from './components/InscriptionOperateur';
import ActivateAccount from "./pages/ActivationCompte";
import ActivateOperateur from "./pages/ActivateOperateur";
import DashboardOperateur from './pages/DashboardOperateur';
import AddAgentForm from "./pages/AddAgentForm";
import DashboardAgent from './pages/DashboardAgent';
import AgentList from "./pages/AgentList";


function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/inscription-operateur" element={<InscriptionOperateur />} />
                <Route path="/activation" element={<ActivateOperateur />} />
                <Route path="/agent/activate" element={<ActivateAccount/>} />
                <Route path="/superviseur/ajouter-agent" element={<AddAgentForm />} />
                <Route path="/dashboard-operateur" element={<DashboardOperateur />} />
                <Route path="/dashboard-agent" element={<DashboardAgent />} />
                <Route path="/agents" element={<AgentList />} />
            </Routes>
        </Router>
    );
}

export default App;