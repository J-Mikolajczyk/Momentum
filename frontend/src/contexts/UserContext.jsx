import { createContext, useContext, useState, useEffect } from 'react';
import setThemeColor from '../hooks/useThemeColor';
import { getRequest, loginRequest, logoutRequest } from '../utils/api';
import { useUI } from '../contexts/UIContext';

export default function App() {}

export const UserContext = createContext();

export const useUser = () => useContext(UserContext);

const ip = import.meta.env.VITE_IP_ADDRESS;

export const UserProvider = ({ children }) => {

  const { toggleSidebar } = useUI();

  const [userInfo, setUserInfo] = useState(null);
  const [loggedIn, setLoggedIn] = useState(false);
  const [showEmailForm, setShowEmailForm] = useState(false);
  const [blockName, setBlockName] = useState(null);
  const [weekText, setWeekText] = useState('Loading...');

  const userId = userInfo?.id ?? null;
  const email = userInfo?.email ?? null;

  const toggleShowEmailForm = () => setShowEmailForm((prev) => !prev);

  const logOut = () => {
    logoutRequest(email);
    setLoggedIn(false);
    setShowEmailForm(true);
    setBlockName(null);
    setWeekText('Loading...');
    setUserInfo(null);
    setThemeColor('#ffffff');
    toggleSidebar();
  };

  const fetchData = async () => {
    try {
      const refreshResponse = await getRequest(ip + '/secure/user/refresh', { userId });
      if (refreshResponse.ok) {
        const json = await refreshResponse.json();
        if (json.exists === false) {
          console.log('User does not exist, redirecting to login');
          return;
        }
        setUserInfo(json);
      } else if ([403, 401].includes(refreshResponse.status)) {
        logOut();
      } else {
        console.log('Response not OK');
      }
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <UserContext.Provider
      value={{
        userInfo,
        setUserInfo,
        loggedIn,
        setLoggedIn,
        showEmailForm,
        setShowEmailForm,
        toggleShowEmailForm,
        logOut,
        blockName,
        setBlockName,
        weekText,
        setWeekText,
        fetchData,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};
