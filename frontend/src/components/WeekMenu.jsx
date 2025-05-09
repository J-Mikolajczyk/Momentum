import { useState } from 'react';

export default function WeekMenu({ blockData, setWeekAndDay, weekText, addWeek, removeWeek }) {
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
      <div className="relative bg-gray-300 h-8 w-full flex items-center px-2">
        <p className="absolute left-1/2 transform -translate-x-1/2 font-anton text-lg cursor-pointer text-center" onClick={toggleDropdown}>{weekText}</p>
        { isDropdownOpen ? (
          <div className="ml-auto flex gap-1 h-2/3">
            <button onClick={removeWeek} className="flex items-center justify-center bg-gray-400 text-gray-500 font-anton aspect-square h-full text-xl border border-gray-500 pr-1">-</button>
            <button onClick={addWeek} className="flex items-center justify-center bg-gray-400 text-gray-500 font-anton aspect-square h-full text-xl border border-gray-500 pr-1">+</button>
          </div>) : (<></>)
        }
        
      </div>


      {isDropdownOpen && blockData?.weeks?.length > 0 && (
        <div className="absolute left-0 right-0 w-full top-8 mx-auto bg-gray-300 shadow-md z-10 overflow-x-auto">
            <div className="flex p-2 justify-around gap-2">
                {blockData.weeks.map((week, weekIndex) => (
                  <div key={`week-col-${weekIndex}`} className="flex flex-col w-1/5 min-w-11 items-center gap-1">
                    <div className="text-center font-anton text-sm text-gray-700"> Week {weekIndex + 1}</div>
                    {Array.from({ length: maxDays }).map((_, dayIndex) => {
                      const dayExists = week.days[dayIndex];
                      const label = `Day ${dayIndex + 1}`;
                      return (
                        <div key={`${weekIndex}-${dayIndex}`} onClick={() => {
                            if (dayExists) {
                              setWeekAndDay(weekIndex + 1, dayIndex + 1);
                              setDropdownOpen(false);
                            }
                          }}
                          className={`h-6 w-full border bg-white border-gray-400 rounded-md text-blue-800 font-anton text-sm flex items-center justify-center cursor-pointer ${
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
    </div>
  );
}
