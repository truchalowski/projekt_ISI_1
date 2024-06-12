import { QueryClient, QueryClientProvider } from 'react-query';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

import './App.css';
import HomePage from './Pages/HomePage/HomePage';
import Login from './Pages/Login/Login';
import Register from './Pages/Register/Register';
import ReservationsActive from './Pages/ReservationsActive/Reservations';
import ServicesPage from './Pages/ServicesPage/ServicesPage';
import Navbar from './components/Navbar/Navbar';
import { UserProvider } from './context/UserContext';

function App() {
  const queryClient = new QueryClient();
  return (
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <UserProvider>
          <Routes>
            <Route path="/" element={<Navbar />}>
              <Route index element={<HomePage />} />
              <Route path="services" element={<ServicesPage />} />
              <Route path="login" element={<Login />} />
              <Route path="register" element={<Register />} />
              <Route
                path="details/active-reservations"
                element={<ReservationsActive />}
              />
            </Route>
          </Routes>
        </UserProvider>
      </QueryClientProvider>
    </BrowserRouter>
  );
}

export default App;
