package seg3x02.employeeGql.resolvers

import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.resolvers.types.CreateEmployeeInput
import java.util.UUID

@Controller
class EmployeesResolver(private val employeesRepository: EmployeesRepository) {

    init {
        try {
            MongoServer(MemoryBackend()).bind("localhost", 27017)
        } catch (_: Exception) {
            // Un serveur MongoDB / Docker écoute déjà sur le port 27017
        }
    }

    @QueryMapping
    fun employees(): List<Employee> {
        return employeesRepository.findAll()
    }

    @QueryMapping
    fun employeeById(@Argument id: String): Employee? {
        return employeesRepository.findById(id).orElse(null)
    }

    @MutationMapping
    fun newEmployee(@Argument("createEmployeeInput") input: CreateEmployeeInput): Employee {
        if (input.name != null &&
            input.dateOfBirth != null &&
            input.city != null &&
            input.salary != null
        ) {
            val employee = Employee(
                input.name,
                input.dateOfBirth,
                input.city,
                input.salary,
                input.gender,
                input.email
            )
            employee.id = UUID.randomUUID().toString()
            employeesRepository.save(employee)
            return employee
        } else {
            throw Exception("Invalid input")
        }
    }

    @MutationMapping
    fun deleteEmployee(@Argument id: String): Boolean {
        employeesRepository.deleteById(id)
        return true
    }

    @MutationMapping
    fun updateEmployee(
        @Argument id: String,
        @Argument("createEmployeeInput") input: CreateEmployeeInput
    ): Employee {
        val emp = employeesRepository.findById(id)
        if (emp.isPresent &&
            input.name != null &&
            input.dateOfBirth != null &&
            input.city != null &&
            input.salary != null
        ) {
            val updated = Employee(
                input.name,
                input.dateOfBirth,
                input.city,
                input.salary,
                input.gender,
                input.email
            )
            updated.id = id
            return employeesRepository.save(updated)
        } else {
            throw Exception("Invalid input")
        }
    }
}