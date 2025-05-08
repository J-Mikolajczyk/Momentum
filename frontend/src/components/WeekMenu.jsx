import {useState} from 'react';

export default function WeekMenu({blockData, setWeekNameAndNum, weekNum, weekText}){

      const [isDropdownOpen, setDropdownOpen] = useState(false);

      const toggleDropdown = () => {
        setDropdownOpen(prev => !prev);
      }

      const selectWeek = (index) => {
        setWeekNameAndNum(index);
        setDropdownOpen(false);
      };


      const incrementWeek = () => {
        setWeekNameAndNum(weekNum+1);
      }
    
      const decrementWeek = () => {
        setWeekNameAndNum(weekNum-1);
      }

      return (
        <div className="relative w-full">
          <div className="flex flex-row bg-gray-300 h-8 w-full justify-around items-center">
            {weekNum < 2 ? (
              <button className="font-anton text-gray-300 text-2xl w-1/10">&lt;</button>
            ) : (
              <button onClick={decrementWeek} className="font-anton text-2xl w-1/10">&lt;</button>
            )}
            <p
              className="font-anton text-lg cursor-pointer"
              onClick={toggleDropdown}
            >
              {weekText}
            </p>
            {!blockData || !blockData.weeks || weekNum === blockData.weeks.length || blockData.weeks.length === 0 ? (
              <button className="font-anton text-gray-300 text-2xl w-1/10">&gt;</button>
            ) : (
              <button onClick={incrementWeek} className="font-anton text-2xl w-1/10">&gt;</button>
            )}
          </div>
    
          {isDropdownOpen && blockData?.weeks?.length > 0 && (
            <ul className="absolute w-full top-8 left-1/2 transform -translate-x-1/2 bg-gray-300 shadow-md z-10">
              {blockData.weeks.map((week, index) => (
                <li
                  key={index}
                  onClick={() => selectWeek(index+1)}
                  className="flex items-center justify-center font-anton text-black text-lg h-8 hover:bg-gray-200 cursor-pointer"
                >
                  Week {index+1}
                </li>
              ))}
            </ul>
          )}
        </div>
      );
}