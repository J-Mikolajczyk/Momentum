import { createContext, useContext, useState } from 'react';
import setThemeColor from '../hooks/useThemeColor';

export const UIContext = createContext();

export const useUI = () => useContext(UIContext);

export const UIProvider = ({ children }) => {
  const [showSidebar, setShowSidebar] = useState(false);
  const [showAddBlockMenu, setShowAddBlockMenu] = useState(false);
  const [showRenamePopup, setShowRenamePopup] = useState(false);
  const [showDeletePopup, setShowDeletePopup] = useState(false);

  const toggleSidebar = () => setShowSidebar((prev) => !prev);

  const toggleAddBlockMenu = () => {
    setShowAddBlockMenu((prev) => {
      const newValue = !prev;
      setThemeColor(newValue ? '#0D1E5C' : '#193cb8');
      return newValue;
    });
  };

  return (
    <UIContext.Provider
      value={{
        showSidebar,
        toggleSidebar,
        showAddBlockMenu,
        toggleAddBlockMenu,
        showRenamePopup,
        setShowRenamePopup,
        showDeletePopup,
        setShowDeletePopup
      }}
    >
      {children}
    </UIContext.Provider>
  );
};
