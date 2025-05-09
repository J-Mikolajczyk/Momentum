import { useState } from 'react';

export default function WeekMenu({ blockData, setDisplayWeekAndDay, weekText, addWeek, removeWeek }) {
  const [isDropdownOpen, setDropdownOpen] = useState(false);

  const toggleDropdown = () => {
    setDropdownOpen(prev => !prev);
  };

  const getMaxDays = () => {
    if (!blockData?.weeks?.length) return 0;
    return Math.max(...blockData.weeks.map(week => week.days.length));
  };

  const maxDays = getMaxDays();

  return (
    <div className="relative w-full">
        <div className="relative bg-blue-800 h-18 w-full flex flex-col justify-center px-4">
          <p className="text-white font-anton text-xl cursor-pointer h-1/2" onClick={toggleDropdown}>{blockData?.name}</p>
          <p className="text-white font-anton text-2xl cursor-pointer h-2/3" onClick={toggleDropdown}>{weekText}</p>
        </div>

        {isDropdownOpen && (
  <div className="absolute top-18 left-0 w-full bg-blue-800 z-20 shadow-lg">
    <div className="px-4 h-10 flex items-end justify-between border-t-1 border-blue-900 pb-1">
      <p className="text-white font-anton text-lg cursor-pointer w-full" onClick={toggleDropdown}>Weeks</p>
      <div className="flex gap-2">
        <button onClick={removeWeek} className="bg-white text-blue-800 font-anton-no-italic h-6 w-8 text-3xl rounded-md border border-gray-500 flex items-center justify-center pb-1.5">-</button>
        <button onClick={addWeek} className="bg-white text-blue-800 font-anton-no-italic h-6 w-8 text-3xl rounded-md border border-gray-500 flex items-center justify-center pb-1.5">+</button>
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
                return (
                  <div
                    key={`${weekIndex}-${dayIndex}`}
                    onClick={() => {
                      if (dayExists) {
                        setDisplayWeekAndDay(weekIndex+1, dayIndex+1);
                        setDropdownOpen(false);
                      }
                    }}
                    className={`pt-0.5 h-6 w-full border bg-white border-gray-400 rounded-md text-blue-800 font-anton text-md flex items-center justify-center cursor-pointer ${
                      dayExists ? 'hover:bg-gray-200' : 'text-gray-400 cursor-default'
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
  </div>);
}
