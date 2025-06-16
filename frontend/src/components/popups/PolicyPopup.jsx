import React, { useEffect } from 'react';
import setThemeColor from '../../hooks/useThemeColor'

export default function PolicyPopup({ type, setType, location }) {
  if (!type) {
    return null;
  }

  useEffect(() => {
    if (location == 'sidebar') {
      setThemeColor('#0D1E5C'); 
    } else if (location == 'email') {
      setThemeColor('#7F7F7F');
    }
  }, []);

  const handleClose = () => {
    setType(null);
    if(location == 'sidebar') {
      setThemeColor('#193cb8');
    } else if (location == 'email') { 
      setThemeColor('ffffff');
    }
  };

  const handleOuterClick = () => {
    handleClose();
  };

  const handleInnerClick = (e) => {
    e.stopPropagation();
  };

  return (
    <div onClick={handleOuterClick} className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div onClick={handleInnerClick} className="bg-white p-2 rounded-xl shadow-lg w-3/4 h-5/6 min-w-50 max-w-90 flex flex-col items-center gap-2 font-anton text-black text-sm">
      <div className="overflow-y-auto scrollbar-hide w-full px-2" style={{ maxHeight: '100%', flexGrow: 1 }}>
        {type == 'terms' ? (<>
          <p className="text-center">Momentum Terms of Service</p> 
            <p className="text-left whitespace-pre-line">
          {`1. Acceptance of Terms
          By using Momentum, you agree to be bound by these Terms. If you do not agree, please do not use the application.
          
          2. Use of the Service
          Momentum is a personal tool to help you track strength training data. You may use the app only for lawful purposes. You agree not to misuse the app or attempt to interfere with its normal operation.
          
          3. Account Responsibility
          If you create an account, you are responsible for maintaining the confidentiality of your login credentials. You agree to notify us immediately of any unauthorized use of your account.
          
          4. Data Usage
          Momentum stores data you enter (e.g., workouts, sets, reps). This data is only used to provide core app functionality and is not shared or sold.
          
          5. Termination
          We reserve the right to suspend or terminate your access to the app at our discretion, particularly if your usage is abusive, illegal, or harmful to the service.
          
          6. Disclaimers
          Momentum is provided “as is” without warranties of any kind. We make no guarantees about the accuracy or reliability of the data or app performance.
          
          7. Limitation of Liability
          To the extent permitted by law, we shall not be liable for any indirect, incidental, or consequential damages resulting from your use of Momentum.
          
          8. Changes to Terms
          We may update these Terms occasionally. Continued use of the app after changes constitutes your acceptance of the new terms.`}</p>
        
        </>
        
        ) : type == 'privacy' ? (<>
            <p className="text-center">Momentum Privacy Policy</p> 
            <p className="text-left whitespace-pre-line">
          {`1. Information We Collect
            We collect the following information when you use the app:

            Your name and email address (used for account creation and login)

            Your encrypted password

            Strength training data you enter (e.g., exercises, sets, reps)

            2. How We Use Your Information
            The information you provide is used solely to:

            Allow you to create and log in to your account

            Store and display your personal strength training data

            We do not collect any location, device, or usage analytics beyond what is required to deliver core functionality.

            3. Cookies and Authentication
            We use cookies to manage authentication sessions securely. These cookies help keep you logged in while protecting your information.
            
            These cookies are not used for tracking or analytics, and they are inaccessible to JavaScript in your browser (HTTP-only) to help protect against XSS attacks.

            No third-party cookies or trackers are used on this app.

            4. Data Security
            We use encryption and best practices to store passwords and authentication tokens securely. While no system is 100% secure, we take reasonable steps to protect your data.

            5. No Third-Party Sharing
            We do not share, sell, or rent your information with third parties under any circumstances.

            6. Account Deletion
            You may request deletion of your account and all associated data at any time. Upon deletion, your data is permanently removed from our systems.

            7. Changes to This Policy
            We may update this Privacy Policy from time to time. Significant changes will be communicated through the app interface. Continued use of the app after updates means you accept the revised policy.`}</p>
        
        </>
        
        ) : (
          <p className="font-anton text-black text-xl mt-2 text-center">Unknown type</p>
        )}
        </div> 
        <div className="flex justify-around w-full">
          <button onClick={() => { handleClose(); }} className="w-1/3 bg-gray-500 font-anton text-white rounded-md hover:bg-gray-600 flex items-center justify-center pr-1 min-w-12 cursor-pointer" >Close</button>
        </div>
      </div>
    </div>
  );
}