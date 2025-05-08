import React, { createContext, useContext, useState, useEffect } from 'react';
import { postRequest, getRequest } from '../utils/api';

const BlockContext = createContext();

export const BlockProvider = ({ blockName, userInfo, children }) => {
  const ip = import.meta.env.VITE_IP_ADDRESS;
  const userId = userInfo?.id || null;

  const [blockData, setBlockData] = useState(null);
  const [weekNum, setWeekNum] = useState(0);

  const refresh = async () => {
    try {
      const response = await getRequest(`${ip}/secure/block/get`, { blockName, userId });
      if (response.ok) {
        const data = await response.json();
        setBlockData(data);
        if (data.weeks.length > 0 && weekNum === 0) {
          setWeekNum(1);
        }
      } else {
        console.error('Failed to fetch block data');
      }
    } catch (error) {
      console.error('Error refreshing block data:', error);
    }
  };

  const update = async () => {
    try {
      const { id, weeks } = blockData;
      const response = await postRequest(`${ip}/secure/block/update`, { id, name: blockName, weeks });
      if (response.ok) {
        refresh();
      } else {
        console.error('Failed to update block');
      }
    } catch (error) {
      console.error('Error updating block:', error);
    }
  };

  useEffect(() => {
    refresh();
  }, []);

  return (
    <BlockContext.Provider value={{ blockData, setBlockData, weekNum, setWeekNum, update, refresh }}>
      {children}
    </BlockContext.Provider>
  );
};

export const useBlockContext = () => useContext(BlockContext);