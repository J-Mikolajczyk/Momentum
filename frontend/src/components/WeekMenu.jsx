
export default function WeekMenu({blockData, setWeekNameAndNum, weekNum, weekText}){

      const incrementWeek = () => {
        setWeekNameAndNum(weekNum+1);
      }
    
      const decrementWeek = () => {
        setWeekNameAndNum(weekNum-1);
      }

      return <div className="flex flex-row bg-gray-300 h-8 w-full justify-around items-center">
            {weekNum < 2 ?
              (<button className="font-anton text-gray-300 text-2xl w-1/10">&lt;</button>) : 
              (<button onClick={decrementWeek} className="font-anton text-2xl w-1/10">&lt;</button>)
            }
            <p className="font-anton text-lg">{weekText}</p>
            {!blockData || !blockData.weeks || weekNum === blockData.weeks.length || blockData.weeks.length === 0 ? 
              (<button className="font-anton text-gray-300 text-2xl w-1/10">&gt;</button>) : 
              (<button onClick={incrementWeek} className="font-anton text-2xl w-1/10">&gt;</button>)
            }
        </div>
}