export default function Navigation ({goHome, toggleSidebar}) {
    return <nav className='bg-blue-800 sticky top-0 z-10 h-1/12 flex shrink-0 justify-between items-center pl-2.5'>
            <button onClick={goHome} className='select-none text-white font-anton text-5xl'>MOMENTUM</button>
            <button onClick={toggleSidebar} className='select-none text-white font-anton text-6xl pb-2 w-1/6'>≡</button>
          </nav>
}