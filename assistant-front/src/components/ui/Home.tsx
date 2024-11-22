import { useNavigate } from "react-router-dom";

export default function Home() {
  const navigate = useNavigate();

  const handleNavigation = () => {
    navigate("/employees");
  };

  return (
    <div className="flex flex-col min-h-screen">
      <header className="flex justify-between items-center p-4 shadow-md">
        <div className="w-30 h-14">
          <img src="src\images\nsu.png" alt="Logo" className="w-full h-full object-cover" />
        </div>
        <div className="flex gap-4">
          <button 
            onClick={handleNavigation} 
            className="px-4 py-2 border border-green-500 text-green-500 rounded-lg"
          >
            Sign In
          </button>
          <button 
            onClick={handleNavigation} 
            className="px-4 py-2 bg-green-500 text-white rounded-lg"
          >
            Sign Up
          </button>
        </div>
      </header>

      <main className="flex flex-col items-center justify-center flex-grow text-center">
        <h1 className="text-6xl font-bold text-gray-800">DepartmentAssistant</h1>
        <p className="mt-4 text-xl text-gray-600">Convenient service for the department</p>
      </main>
    </div>
  );
}
