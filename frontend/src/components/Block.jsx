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

  const openDay = (index) => {
    setDayIndex(index);
  };

  const setWeekAndDay = (weekNum, dayNum) => {
    setWeekNum(weekNum);
    setDayIndex(dayNum - 1); 
    setWeekText(`Week ${weekNum} Day ${dayNum}`);
  };

  const addWeek = async () => {
    if (blockData.weeks.length >= 6) {
      setMessage("Mesocycles longer than 6 weeks are not recommended. Please consider a deload.");
      return;
    }
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



  const update = async () => {
    const id = blockData.id;
    const name = blockName;
    const weeks = blockData.weeks;
    try {
       const updateResponse = await postRequest(ip + '/secure/block/update', { name, id, weeks });
       if (updateResponse.ok) {
         refresh();
       } else {
         console.log('Issue updating block');
       }
     } catch (err) {
       console.log(err);
     }
  }

  const refresh = async () => {
    try {
      const refreshResponse = await getRequest(ip + '/secure/block/get', { blockName, userId });
      if (refreshResponse.ok) {
        const json = await refreshResponse.json();
        if (json.exists === false) {
          console.log('Issue with block data, block does not exist');
          return;
        }
        setBlockData(json);
        setWeekAndDay(weekNum, dayIndex+1);
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
      <MessagePopup message={message} setMessage={setMessage} proceedToAddWeek={proceedToAddWeek}/>
      <div className="flex flex-row w-full justify-between items-center mb-3">
        <p className="text-blue-800 font-anton inline-block text-3xl">{blockName}</p>
        <button onClick={addWeek} className="inline-block select-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs">Add Week</button>
      </div>
      <WeekMenu blockData={blockData} setWeekAndDay={setWeekAndDay} weekText={weekText}/>
      <div className="flex flex-row w-full flex-grow items-start gap-4 pb-8">
          <div className="flex flex-col items-center p-2 rd w-full">
            <Day blockData={blockData} dayNum={dayIndex} weekNum={weekNum - 1} update={update} setDayIndex={setDayIndex} />
          </div>
      </div>
    </>
  );
}