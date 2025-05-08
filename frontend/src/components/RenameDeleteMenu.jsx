export default function RenameDeleteMenu ({renameMethod, deleteMethod, index, ref}) {

    return <ul ref={ref} className="absolute top-full right-4 w-32 bg-gray-300 shadow-md z-10 rounded divide-y divide-gray-400 border-gray-400 border-1">
                <li onClick={() => renameMethod(index)} className="flex items-center justify-center font-anton text-black text-lg h-8 hover:bg-gray-200 cursor-pointer">Rename</li>
                <li onClick={() => deleteMethod(index)} className="flex items-center justify-center font-anton text-black text-lg h-8 hover:bg-gray-200 cursor-pointer">Delete</li>
            </ul>
}