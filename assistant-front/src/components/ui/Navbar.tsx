import { Link, useLocation } from "react-router-dom";
import { GrUserManager } from "react-icons/gr";
import { MdOutlineCalendarMonth } from "react-icons/md";
import { ImTree } from "react-icons/im";
import { GrDocumentNotes } from "react-icons/gr";

const Navbar: React.FC = () => {
  const location = useLocation();

  const getActiveClass = (path: string) =>
    location.pathname.startsWith(path) ? "bg-green-500 text-white" : "text-gray-700";

  return (
    <div className="flex justify-around p-4 bg-gray-500 sticky top-0 z-10 ">
      <div className="flex gap-4 p-2 bg-gray-100 rounded-full shadow-md">
        <Link to="/employees">
          <button
            className={`px-4 py-2 rounded-full transition-all duration-300 ${getActiveClass(
              "/employees"
            )}`}
          >
            <GrUserManager />
          </button>
        </Link>
        <Link to="/processes">
          <button
            className={`px-4 py-2 rounded-full transition-all duration-300 ${getActiveClass(
              "/processes"
            )}`}
          >
            <ImTree />
          </button>
        </Link>
        <Link to="/calendar">
          <button
            className={`px-4 py-2 rounded-full transition-all duration-300 ${getActiveClass(
              "/calendar"
            )}`}
          >
            <MdOutlineCalendarMonth />
          </button>
        </Link>
        <Link to="/documents">
          <button
            className={`px-4 py-2 rounded-full transition-all duration-300 ${getActiveClass(
              "/documents"
            )}`}
          >
            <GrDocumentNotes />
          </button>
        </Link>
      </div>
    </div>
  );
};

export default Navbar;
