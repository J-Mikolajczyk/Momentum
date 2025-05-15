export default function Navigation ({goHome, toggleSidebar}) {
    return <nav className='bg-blue-800 sticky top-0 z-10 h-1/12 min-h-15 flex shrink-0 justify-between items-center pl-2.5'>
            <p className='text-white font-anton-italic text-5xl'>MOMENTUM</p>
            <button onClick={toggleSidebar} className='text-white font-anton text-6xl pb-2 w-1/6 cursor-pointer'>≡</button>
          </nav>
}