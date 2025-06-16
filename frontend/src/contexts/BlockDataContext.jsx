import { createContext, useContext } from 'react';

export const BlockDataContext = createContext(null);

export const useBlockDataContext = () => {
  const context = useContext(BlockDataContext);
  if (!context) {
    throw new Error("useBlockDataContext must be used within a BlockDataProvider");
  }
  return context;
};
