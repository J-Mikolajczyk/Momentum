import {useState} from 'react';
import AddDayPopup from './AddDayPopup';

export default function DayDashboard({blockData, openDay, weekNum, setBlockData, blockName, update}){

    const [showAddDayPopup, setShowAddDayPopup] = useState(false);

    const toggleAddDayPopup = () => {
        setShowAddDayPopup(!showAddDayPopup);
    }

    return <>
        <AddDayPopup show={showAddDayPopup} toggle={toggleAddDayPopup} blockData={blockData} setBlockData={setBlockData} blockName={blockName} weekNum={weekNum} update={update}/>
      
        {blockData?.weeks?.length > 0 && (
            <>
            {blockData.weeks[weekNum - 1]?.days?.length > 0 ? (
                blockData.weeks[weekNum - 1].days.map((day, index) => (
                <button
                    onClick={() => openDay(index)}
                    key={index}
                    className="bg-blue-50 text-blue-800 font-anton px-4 py-2 rounded-md shadow-md w-full text-left text-2xl border-blue-800 border-1"
                >
                    {day.name}
                </button>
                ))
            ) : (
                <div className="flex flex-row w-full items-center justify-between">
                <p className="text-gray-500 font-anton text-2xl">No Days Created</p>
                </div>
            )}

            <button onClick={toggleAddDayPopup} className="flex elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-21 h-6 text-l border items-center justify-center border-gray-500 rounded-xs ml-auto">Add Day</button>
         </>
        )
      }
    </>
}