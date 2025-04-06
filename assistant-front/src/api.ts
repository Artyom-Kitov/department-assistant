import axios, { AxiosInstance } from "axios";

const BASE_URL = "http://localhost:8080/api/v1";

const apiClient: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export interface Contacts {
  id: number;
  phoneNumber: string;
  email: string;
  nsuEmail: string;
  additionalInfo: string;
}

interface AcademicDegree {
  id: number;
  name: string;
}

interface EmploymentStatus {
  id: number;
  isEmployedInNsu: boolean;
  employmentInfo: string;
}

interface EmploymentRecord {
  id: number;
  dateOfReceipt: string;
}

export interface PassportInfo {
  id: number;
  passportInfo: string;
}

interface WorkExperience {
  id: number;
  days: number;
}

interface CertificateOfNoCriminalRecord {
  id: number;
  dateOfReceipt: string;
  expirationDate: string;
}

export interface Employee {
  id: string;
  firstName: string;
  lastName: string;
  middleName: string;
  agreement: boolean;
  hasCompletedAdvancedCourses: boolean;
  hasHigherEducation: boolean;
  needsMandatoryElection: boolean;
  snils: string | null;
  inn: string | null;
  isArchived: boolean;
}

// Функция для получения списка сотрудников
export const getEmployees = async (): Promise<Employee[]> => {
  try {
    const response = await apiClient.get("/employees");
    return response.data;
  } catch (error) {
    console.error("Error fetching employees:", error);
    throw error;
  }
};

// Функция для получения информации о сотруднике по ID
export const getEmployeeById = async (id: string): Promise<Employee> => {
  try {
    const response = await apiClient.get(`/employees/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching employee with ID ${id}:`, error);
    throw error;
  }
};

export const getEmployeesInfo = async (): Promise<Employee[]> => {
  try {
    const response = await apiClient.get("/employees/info");
    return response.data;
  } catch (error) {
    console.error("Error fetching employees info:", error);
    throw error;
  }
};

export const getEmployeeInfoById = async (id: string): Promise<Employee> => {
  try {
    const response = await apiClient.get(`/employees/info/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching info for employee with ID ${id}:`, error);
    throw error;
  }
};

// Создание сотрудника
export const createEmployee = async (employee: Employee): Promise<Employee> => {
  try {
    console.log("Creating employee with data:", employee);
    const response = await apiClient.post("/employees", employee);
    return response.data;
  } catch (error) {
    console.error("Error creating employee:", error);
    throw error;
  }
};

export const createContactsForEmployee = async (
  employeeId: string,
  contacts: Partial<Contacts>
): Promise<Contacts> => {
  try {
    const response = await apiClient.post(
      `contacts/create/employee`, // Endpoint without employeeId in the path
      contacts,
      {
        params: { employeeId }, // Pass employeeId as a query parameter
      }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error creating contacts for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const updateContactsForEmployee = async (
  employeeId: string,
  contacts: Partial<Contacts>
): Promise<Contacts> => {
  try {
    const response = await apiClient.put(
      `/contacts/update/employee`, 
      contacts,
      {
        params: { employeeId }, 
      }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error updating contacts for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const createAcademicDegreeForEmployee = async (
  employeeId: string,
  academicDegree: Partial<AcademicDegree>
): Promise<AcademicDegree> => {
  try {
    const response = await apiClient.post(
      `/academic-degree/create`,
      academicDegree,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error creating academic degree for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const updateAcademicDegreeForEmployee = async (
  employeeId: string,
  academicDegree: Partial<AcademicDegree>
): Promise<AcademicDegree> => {
  try {
    const response = await apiClient.put(
      `/academic-degree/update`,
      academicDegree,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error updating academic degree for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const createEmploymentRecordForEmployee = async (
  employeeId: string,
  employmentRecord: Partial<EmploymentRecord>
): Promise<EmploymentRecord> => {
  try {
    const response = await apiClient.post(
      `/employment-records/create`,
      employmentRecord,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error creating employment record for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const updateEmploymentRecordForEmployee = async (
  employeeId: string,
  employmentRecord: Partial<EmploymentRecord>
): Promise<EmploymentRecord> => {
  try {
    const response = await apiClient.put(
      `/employment-records/update`,
      employmentRecord,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error updating employment record for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const createEmploymentStatusForEmployee = async (
  employeeId: string,
  employmentStatus: Partial<EmploymentStatus>
): Promise<EmploymentStatus> => {
  try {
    const response = await apiClient.post(
      `/employment-status/create`,
      employmentStatus,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error creating employment status for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const updateEmploymentStatusForEmployee = async (
  employeeId: string,
  employmentStatus: Partial<EmploymentStatus>
): Promise<EmploymentStatus> => {
  try {
    const response = await apiClient.put(
      `/employment-status/update`,
      employmentStatus,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error updating employment status for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

// Создание типа занятости для сотрудника
export const createEmploymentTypeForEmployee = async (
  employeeId: string,
  employmentType: { name: string }
): Promise<{ name: string }> => {
  try {
    const response = await apiClient.post(
      `/employment-type/create/${employeeId}`,
      employmentType
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error creating employment type for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const createNoCriminalCertificateForEmployee = async (
  employeeId: string,
  certificate: Partial<CertificateOfNoCriminalRecord>
): Promise<CertificateOfNoCriminalRecord> => {
  try {
    const response = await apiClient.post(
      `/no-criminal-certificate/create`,
      certificate,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error creating no criminal certificate for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const updateNoCriminalCertificateForEmployee = async (
  employeeId: string,
  certificate: Partial<CertificateOfNoCriminalRecord>
): Promise<CertificateOfNoCriminalRecord> => {
  try {
    const response = await apiClient.put(
      `/no-criminal-certificate/update`,
      certificate,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error updating no criminal certificate for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const createPassportInfoForEmployee = async (
  employeeId: string,
  passportInfo: Partial<PassportInfo>
): Promise<PassportInfo> => {
  try {
    const response = await apiClient.post(
      `/passport-info/create`, // Endpoint without employeeId in the path
      passportInfo,
      {
        params: { employeeId }, // Pass employeeId as a query parameter
      }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error creating passport info for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const updatePassportInfoForEmployee = async (
  employeeId: string,
  passportInfo: Partial<PassportInfo>
): Promise<PassportInfo> => {
  try {
    const response = await apiClient.put(
      `/passport-info/update`, // Endpoint without employeeId in the path
      passportInfo,
      {
        params: { employeeId }, // Pass employeeId as a query parameter
      }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error updating passport info for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

// Создание должности
export const createPost = async (post: {
  name: string;
}): Promise<{ name: string }> => {
  try {
    const response = await apiClient.post("/posts/create", post);
    return response.data;
  } catch (error) {
    console.error("Error creating post:", error);
    throw error;
  }
};

// Обновление должности
export const updatePost = async (
  postId: string,
  post: { name: string }
): Promise<{ name: string }> => {
  try {
    const response = await apiClient.put(`/posts/update/${postId}`, post);
    return response.data;
  } catch (error) {
    console.error(`Error updating post with ID ${postId}:`, error);
    throw error;
  }
};

export const createWorkExperienceForEmployee = async (
  employeeId: string,
  workExperience: Partial<WorkExperience>
): Promise<WorkExperience> => {
  try {
    const response = await apiClient.post(
      `/work-experience/create`,
      workExperience,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error creating work experience for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const updateWorkExperienceForEmployee = async (
  employeeId: string,
  workExperience: Partial<WorkExperience>
): Promise<WorkExperience> => {
  try {
    const response = await apiClient.put(
      `/work-experience/update`,
      workExperience,
      { params: { employeeId } }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error updating work experience for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

// Обновление сотрудника
export const updateEmployee = async (
  id: string,
  employee: Employee
): Promise<Employee> => {
  try {
    console.log("Updating employee with ID:", id);
    console.log("Data being sent:", employee);

    const response = await apiClient.put(`/employees/${id}`, employee);
    console.log("Response from server:", response.data);
    return response.data;
  } catch (error) {
    console.error(`Error updating employee with ID ${id}:`, error);
    throw error;
  }
};

// Удаление сотрудника
export const deleteEmployee = async (id: string): Promise<void> => {
  try {
    await apiClient.delete(`/employees/${id}`);
    console.log(`Employee with ID ${id} deleted successfully.`);
  } catch (error) {
    console.error(`Error deleting employee with ID ${id}:`, error);
    throw error;
  }
};

interface TemplateStep {
  id: number;
  type: number;
  duration: number;
  metaInfo: string;
  description: string;
  data: Record<string, any>;
}

interface Template {
  id: string;
  name: string;
  duration: number;
  steps: TemplateStep[];
}

interface TemplateSummary {
  id: string;
  name: string;
  duration: number;
}

// Получение списка всех шаблонов
export const getTemplates = async (): Promise<TemplateSummary[]> => {
  try {
    const response = await apiClient.get("/templates");
    return response.data;
  } catch (error) {
    console.error("Error fetching templates:", error);
    throw error;
  }
};

// Получение шаблона по ID
export const getTemplateById = async (id: string): Promise<Template> => {
  try {
    const response = await apiClient.get(`/templates/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching template with ID ${id}:`, error);
    throw error;
  }
};

// Получение длительности шаблона по ID
export const getTemplateDurationById = async (id: string): Promise<number> => {
  try {
    const response = await apiClient.get(`/templates/duration/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching template duration for ID ${id}:`, error);
    throw error;
  }
};

// Создание нового шаблона
export const createTemplate = async (
  template: Omit<Template, "id">
): Promise<Template> => {
  try {
    console.log(template);
    const response = await apiClient.post("/templates", template);
    return response.data;
  } catch (error) {
    console.error("Error creating template:", error);
    throw error;
  }
};

// Обновление шаблона по ID
export const updateTemplate = async (
  id: string,
  template: Omit<Template, "id" | "duration">
): Promise<Template> => {
  try {
    const response = await apiClient.put(`/templates/${id}`, template);
    return response.data;
  } catch (error) {
    console.error(`Error updating template with ID ${id}:`, error);
    throw error;
  }
};

// Удаление шаблона по ID
export const deleteTemplate = async (id: string): Promise<void> => {
  try {
    await apiClient.delete(`/templates/${id}`);
    console.log(`Template with ID ${id} deleted successfully.`);
  } catch (error) {
    console.error(`Error deleting template with ID ${id}:`, error);
    throw error;
  }
};












//-------------------------- ВЫПОЛНЕНИЕ ПРОЦЕССА -------------------------------

export const executeSubstep = async (
  employeeId: string,
  startProcessId: string,
  substepId: string
): Promise<void> => {
  try {
    const response = await apiClient.post("/execute/substep", {
      employeeId,
      startProcessId,
      substepId,
    });
    return response.data;
  } catch (error) {
    console.error(
      `Error executing substep for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const executeSubstepStatuses = async (
  processId: string,
  employeeId: string
): Promise<void> => {
  try {
    const response = await apiClient.post("/execute/substep/statuses", {
      processId,
      employeeId,
    });
    return response.data;
  } catch (error) {
    console.error(
      `Error fetching substep statuses for process ID ${processId}:`,
      error
    );
    throw error;
  }
};

export const executeStatuses = async (
  processId: string,
  employeeId: string
): Promise<void> => {
  try {
    const response = await apiClient.post("/execute/statuses", {
      processId,
      employeeId,
    });
    return response.data;
  } catch (error) {
    console.error(
      `Error fetching statuses for process ID ${processId}:`,
      error
    );
    throw error;
  }
};

export const startProcess = async (
  employeeId: string,
  processId: string,
  deadline: string
): Promise<void> => {
  try {
    const response = await apiClient.post("/execute/start", {
      employeeId,
      processId,
      deadline,
    });
    return response.data;
  } catch (error) {
    console.error(
      `Error starting process for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const executeConditional = async (
  employeeId: string,
  startProcessId: string,
  processId: string,
  stepId: number,
  successful: boolean
): Promise<void> => {
  try {
    const response = await apiClient.post("/execute/conditional", {
      employeeId,
      startProcessId,
      processId,
      stepId,
      successful,
    });
    return response.data;
  } catch (error) {
    console.error(
      `Error executing conditional step for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const executeCommon = async (
  employeeId: string,
  startProcessId: string,
  processId: string,
  stepId: number
): Promise<void> => {
  try {
    const response = await apiClient.post("/execute/common", {
      employeeId,
      startProcessId,
      processId,
      stepId,
    });
    return response.data;
  } catch (error) {
    console.error(
      `Error executing common step for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};

export const cancelProcess = async (
  employeeId: string,
  processId: string
): Promise<void> => {
  try {
    const response = await apiClient.delete("/execute/cancel", {
      data: { employeeId, processId },
    });
    return response.data;
  } catch (error) {
    console.error(
      `Error canceling process for employee with ID ${employeeId}:`,
      error
    );
    throw error;
  }
};
