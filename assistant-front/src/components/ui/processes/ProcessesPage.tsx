import { Link } from "react-router-dom";
import Navbar from "../Navbar";
import { FaPlusSquare } from "react-icons/fa";
import { ImTree } from "react-icons/im";

export default function ProcessesPage() {

  return (
    <div>
      
      <Navbar/>

      <div className="p-8">
        
        <h2 className="text-2xl font-semibold mb-4">New Process</h2>
        <div className="flex items-left justify-start mb-8">
          <Link to="/processes/createprocess/newprocess">
            <div className="flex flex-col items-center justify-center w-36 h-36 bg-gray-100 rounded-lg shadow-md hover:bg-gray-200 transition">
              <FaPlusSquare className="text-gray-500 text-4xl mb-4" />
              <p className="text-lg font-semibold text-gray-600">New Process</p>
            </div>
          </Link>
        </div>

        <h2 className="text-2xl font-semibold mb-4">Saved Templates</h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
          {Array.from({ length: 9 }).map((_, index) => (
            <div
              key={index}
              className="flex flex-col items-center justify-center w-48 h-48 bg-gray-100 rounded-lg shadow-md hover:bg-gray-200 transition"
            >
              <ImTree className="text-gray-500 text-4xl mb-4" />
              <p className="text-lg font-semibold text-gray-600">Process {index + 1}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
