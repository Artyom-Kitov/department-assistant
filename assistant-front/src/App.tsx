import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./components/ui/Home";
import EmployeesPage from "./components/ui/employees/EmployeesPage";
import CurrentEmployeePage from "./components/ui/employees/CurrentEmployeePage";
import EmployeeFormPage from "./components/ui/employees/EmployeeFormPage";
import CalendarPage from "./components/ui/calendar/CalendarPage";
import ProcessesPage from "./components/ui/processes/ProcessesPage";
import NewProcessPage from "./components/ui/processes/NewProcessPage";
import CurrentProcesses from "./components/ui/processes/CurrentProcesses";
import DocumentsPage from "./components/ui/documents/DocumentsPage";

function App() {
  return (
    <Router>
      <div>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/employees" element={<EmployeesPage />} />
          <Route
            path="/employees/currentemployee"
            element={<CurrentEmployeePage />}
          />
          <Route
            path="/employees/employeeform"
            element={<EmployeeFormPage />}
          />
          <Route path="/calendar" element={<CalendarPage />} />
          <Route path="/processes/createprocess" element={<ProcessesPage />} />
          <Route path="/processes" element={<CurrentProcesses />} />
          <Route
            path="/processes/createprocess/process-editor"
            element={<NewProcessPage />}
          />
          <Route path="/documents" element={<DocumentsPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
