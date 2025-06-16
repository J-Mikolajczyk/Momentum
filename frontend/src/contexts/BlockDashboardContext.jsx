import { createContext, useContext } from 'react';

export const BlockDashboardContext = createContext(null);

export const useBlockDashboardContext = () => {
  const context = useContext(BlockDashboardContext);
  if (!context) {
    throw new Error("useBlockDashboardContext must be used within a BlockDashboardProvider");
  }
  return context;
};
