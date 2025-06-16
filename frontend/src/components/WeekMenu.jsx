import React, { useState } from 'react';
import MessagePopup from './MessagePopup';
import Week from '../models/Week';
import { useBlockDataContext } from '../contexts/BlockDataContext';

export default function WeekMenu({ }) {
  const {
    blockData,
    setWeekAndDay,
    currentWeekIndex,
    currentDayIndex,
    weekText,
    updateWeeks
  } = useBlockDataContext();
  
  const [isDropdownOpen, setDropdownOpen] = useState(false);

  const [message, setMessage] = useState(null);
  const [ignoreMethod, setIgnoreMethod] = useState(null);

  const toggleDropdown = () => {
    setDropdownOpen(prev => !prev);
  };

  const getMaxDays = () => {
    if (!blockData?.weeks?.length) return 0;
    return Math.max(...blockData.weeks.map(week => week.days.length));
  };

  const addWeek = async () => {
      if (!blockData) return;
  
      if (blockData.weeks.length >= 6) {
        setMessage('Mesocycles longer than 6 weeks are not recommended.');
        setIgnoreMethod(() => () => proceedAddWeek());
        return;
      }
      proceedAddWeek();
    };
  
    const proceedAddWeek = async () => {
      const newWeeks = [...blockData.weeks];
      const lastWeek = newWeeks[newWeeks.length - 1];

        const newDays = (lastWeek?.days || []).map(day => {
          const newDay = {
            ...day,
            logged: false,
            exercises: (day.exercises || []).map(ex => ({
              ...ex,
              sets: (ex.sets || []).map(() => ({
                weight: '',
                reps: ''
              }))
            }))
          };
          return newDay;
        });

        newWeeks.push(new Week(newDays));
        await updateWeeks(newWeeks);
      };

    const removeWeek = async () => {
      if (!blockData || blockData.weeks.length === 1) {
        setMessage('Cannot remove only week.');
        return;
      }

      let week = blockData?.weeks[blockData?.weeks.length-1];
        for (const day of week?.days) {
          if (day?.logged) {
            setMessage('Cannot remove a week that contains a logged day.');
            return;
          }
        }
      
  
      if (blockData.weeks.length <= 4) {
        setMessage('Mesocycles shorter than 4 weeks are not recommended.');
        setIgnoreMethod(() => () => proceedRemoveWeek());
        return;
      }
      proceedRemoveWeek();
  
    };
  
    const proceedRemoveWeek = async () => {
      const newWeeks = blockData.weeks.slice(0, -1);
      await updateWeeks(newWeeks);
    };

  const maxDays = getMaxDays();

  return (
    <><MessagePopup message={message} setMessage={setMessage} ignoreMethod={ignoreMethod} setIgnoreMethod={setIgnoreMethod} buttonText="Ignore"/>
      
    <div className="relative bg-blue-800 w-full flex flex-col justify-center">
          <p className="text-white font-anton text-xl cursor-pointer h-6 px-4" onClick={toggleDropdown}>{blockData?.name}</p>
            <p className="text-white font-anton text-2xl cursor-pointer h-10 flex items-center px-4" onClick={toggleDropdown}>{weekText}
              <span className='ml-auto font-anton text-xl'>{isDropdownOpen ? '▲' : '▼'}</span>
            </p>


        {isDropdownOpen && (
        <div className="absolute top-15 left-0 w-full bg-blue-800 z-4 shadow-lg">
          <div className="px-4 h-10 flex items-end justify-between border-t-1 border-blue-900 pb-1">
            <p className="text-white font-anton text-lg w-full">Weeks</p>
            <div className="flex gap-2">
              <button onClick={removeWeek} className="bg-white text-blue-800 font-anton h-6 w-8 text-3xl rounded-md border border-gray-500 flex items-center justify-center pb-1.5 cursor-pointer">-</button>
              <button onClick={addWeek} className="bg-white text-blue-800 font-anton h-6 w-8 text-3xl rounded-md border border-gray-500 flex items-center justify-center pb-1.5 cursor-pointer">+</button>
            </div>
          </div>

          {blockData?.weeks?.length > 0 && (
            <div className="w-full bg-blue-800 shadow-md overflow-x-auto border-t border-blue-800">
              <div className="flex p-1 justify-around gap-1">
                {blockData.weeks.map((week, weekIndex) => (
                  <div key={`week-col-${weekIndex}`} className="flex flex-col w-1/5 min-w-11 items-center gap-1">
                    <div className="text-center font-anton text-md text-white">{weekIndex + 1}</div>
                    {Array.from({ length: maxDays }).map((_, dayIndex) => {
                      const dayExists = week.days[dayIndex];
                      const label = `${(week.days[dayIndex].name)?.substring(0,3).toUpperCase()}`;
                      const isCurrent = currentWeekIndex === weekIndex && currentDayIndex === dayIndex;
                      const isLogged = dayExists.logged;
                      return (
                        <div
                          key={`${weekIndex}-${dayIndex}`}
                          onClick={() => {
                            if (dayExists) {
                              setWeekAndDay(weekIndex, dayIndex);
                              setDropdownOpen(false);
                            }
                          }}
                              className={`pt-0.5 h-6 w-full border font-anton text-md flex items-center justify-center rounded-md cursor-pointer
                              ${
                                dayExists
                                  ? isCurrent
                                    ? 'bg-blue-600 border-blue-900 text-white'
                                    : isLogged
                                    ? 'bg-green-300 border-green-500 text-green-900 hover:bg-green-400'
                                    : 'bg-white border-gray-400 text-blue-800 hover:bg-gray-200'
                                  : 'text-gray-400 bg-white border-gray-300 cursor-default'
                              }`}

                          >
                          {label}
                        </div>
                      );
                    })}
                  </div>
                ))}
              </div>
            </div>
          )}
    </div>)}
  </div></>);
}
