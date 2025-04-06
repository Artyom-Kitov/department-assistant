import Navbar from "../Navbar";
import { Link, useLocation } from "react-router-dom";
import { FaArrowLeft } from "react-icons/fa";
import EmployeeForm from "./EmployeeForm";
import ContactsForm from "./ContactsForm";
import PassportForm from "./PassportForm";
import { useEffect } from "react";
import AcademicDegreeForm from "./AcademicDegreeForm";
// import PostForm from "./PostForm";
import WorkExperienceForm from "./WorkExperienceForm";
import EmploymentRecordForm from "./EmploymentRecordForm";
import CertificateOfNoCriminalRecordForm from "./CertificateOfNoCriminalRecordForm";
import EmploymentStatusForm from "./EmploymentStatusForm";

export default function EmployeeFormPage() {
  const location = useLocation();
  const employee = location.state?.employee;

  useEffect(() => console.log(employee), []);

  return (
    <div>
      <Navbar />
      <div className="p-4">
        <div className="bg-gray-200 p-4 rounded-lg mb-4">
          <Link to="/employees">
            <FaArrowLeft />
          </Link>
        </div>
        <div className="flex justify-between">
          <div className="w-1/3 space-y-6">
            <EmployeeForm employee={employee} />
            <EmploymentStatusForm employeeId={employee.id} employmentStatus={employee.employmentStatus} />
          </div>
          
          <div className="w-1/3 ml-6 space-y-6">
            {/* <PostForm employeeId={employee.id} post={employee} /> */}
            <ContactsForm
              employeeId={employee.id}
              contacts={employee.contacts}
            />
            <WorkExperienceForm
              employeeId={employee.id}
              workExperience={employee.workExperience}
            />
            <AcademicDegreeForm
              employeeId={employee.id}
              academicDegree={employee.academicDegree}
            />
          </div>
          <div className="w-1/3 ml-6 space-y-6">
            <PassportForm
              employeeId={employee.id}
              passportInfo={employee.passportInfo}
            />
            <CertificateOfNoCriminalRecordForm
              employeeId={employee.id}
              certificateData={employee.certificateOfNoCriminalRecord}
            />

            <EmploymentRecordForm
              employeeId={employee.id}
              employmentRecord={employee.employmentRecord}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
