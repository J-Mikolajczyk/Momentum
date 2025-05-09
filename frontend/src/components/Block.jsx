import React, { useState, useEffect } from 'react';
import { postRequest, getRequest } from '../utils/api';

import WeekMenu from './WeekMenu';
import DayDashboard from './DayDashboard';
import Day from './Day';
import Week from '../models/Week';
import MessagePopup from './MessagePopup'

export default function Block({ blockName, userInfo }) {

  const ip = import.meta.env.VITE_IP_ADDRESS;

  const email = userInfo?.email != null ? userInfo.email : null;
  const userId = userInfo?.id != null ? userInfo.id : null;

  const [blockData, setBlockData] = useState(null);
  const [weekNum, setWeekNum] = useState(1);
  const [showAddDayPopup, setShowAddDayPopup] = useState(false);
  const [dayIndex, setDayIndex] = useState(0);
  const [weekText, setWeekText] = useState('Loading...');
  const [message, setMessage] = useState(false);
  const [ignoreMethod, setIgnoreMethod] = useState(null);


  const setWeekAndDay = (weekNum, dayNum) => {
    setWeekNum(weekNum);
    setDayIndex(dayNum - 1); 
    setWeekText(`Week ${weekNum} Day ${dayNum}`);
  };

  const addWeek = async () => {
    if (blockData.weeks.length >= 6) {
      setMessage("Mesocycles longer than 6 weeks are not recommended. Please consider a deload.");
      setIgnoreMethod(() => proceedToAddWeek);
      return;
    }
    setIgnoreMethod(null);
    proceedToAddWeek();
    
  };

  const proceedToAddWeek = () => {
    if (blockData.weeks.length === 0 || blockData.weeks[weekNum - 1].length === 0) {
      blockData.weeks.push(new Week());
    } else {
      blockData.weeks.push(new Week(blockData.weeks[weekNum - 1].days));
    }
    update();
  };

  const removeWeek = async () => {
    if (blockData.weeks.length === 1) {
      setMessage("Cannot remove only week in training block.");
      setIgnoreMethod(() => null);
      return;
    } else if (blockData.weeks.length <= 4) {
      setMessage("Mesocycles less than 4 weeks are not recommended.");
      setIgnoreMethod(() => proceedToRemoveWeek);
      return;
    }
    proceedToRemoveWeek();
    setIgnoreMethod(null);
  };
  
  const proceedToRemoveWeek = () => {
    if (blockData.weeks.length > 1) {
      blockData.weeks.pop();
  
      const newWeekNum = Math.min(weekNum, blockData.weeks.length);
      setWeekNum(newWeekNum);
  
      update(newWeekNum, dayIndex + 1);
    }
  };
  


  const update = async (newWeekNum = weekNum, newDayNum = dayIndex + 1) => {
    const id = blockData.id;
    const name = blockName;
    const weeks = blockData.weeks;
    try {
      const updateResponse = await postRequest(ip + '/secure/block/update', { name, id, weeks });
      if (updateResponse.ok) {
        refresh(newWeekNum, newDayNum);
      } else {
        console.log('Issue updating block');
      }
    } catch (err) {
      console.log(err);
    }
  };
  

  const refresh = async (newWeekNum = weekNum, newDayNum = dayIndex + 1) => {
    try {
      const refreshResponse = await getRequest(ip + '/secure/block/get', { blockName, userId });
      if (refreshResponse.ok) {
        const json = await refreshResponse.json();
        if (json.exists === false) {
          console.log('Issue with block data, block does not exist');
          return;
        }
        const week = json.weeks?.[newWeekNum - 1];
        const day = week?.days?.[newDayNum - 1];
        const dayName = day?.name || `Day ${newDayNum}`;

        setBlockData(json);
        setWeekNum(newWeekNum);
        setDayIndex(newDayNum - 1);
        setWeekText(('Week ' + newWeekNum + ' Day ' + newDayNum + ' ' + dayName));
      } else {
        console.log('Non-OK response');
      }
    } catch (err) {
      console.log(err);
    }
  };

  

  useEffect(() => {
    refresh(); 
  }, []); 

  return (
    <>
      <MessagePopup message={message} setMessage={setMessage} ignoreMethod={ignoreMethod}/>

      <WeekMenu blockData={blockData} setWeekAndDay={setWeekAndDay} weekText={weekText} addWeek={addWeek} removeWeek={removeWeek} refresh={refresh}/>
      <div className="flex flex-row w-full flex-grow items-start gap-4 pb-8 px-4">
          <div className="flex flex-col items-center rd w-full">
            <Day blockData={blockData} dayNum={dayIndex} weekNum={weekNum - 1} update={update} setDayIndex={setDayIndex} />
          </div>
      </div>
    </>
  );
}