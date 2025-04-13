function Home({ userInfo }) {
    return (
      <div className='h-screen flex flex-col bg-gradient-to-b from-blue-400 to-blue-900'>
        {userInfo?.name && <p className='text-white font-anton text-5xl'>Welcome {userInfo.name}</p>}
      </div>
    );
  }
  
  
  export default Home;
  