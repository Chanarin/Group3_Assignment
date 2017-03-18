package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import model.Customer;
import util.DBCP2DataSourceUtils;

public class CustomerDao {

	private static JdbcTemplate jdbcTemplate = new JdbcTemplate(DBCP2DataSourceUtils.getDataSource());

	public CustomerDao() {}

	public Customer getById(Integer id) throws SQLException {
		if (id == null) {
			return null;
		}
		Customer customer = null;
		String sql = "SELECT * FROM tbl_customer WHERE cus_id = ?";
		System.out.println(sql);
        customer = jdbcTemplate.queryForObject(
                sql,
                new Object[]{id},
                new CustomerMapper());
        System.out.println(customer);
		return customer;
	}
	
	public List<Customer> getAll(int limit, int offset, String search){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT SQL_CALC_FOUND_ROWS "
				+ "t.* FROM tbl_customer t ");
		if(search != "" && search != null){
			// fitler by phone number or email address
			sql.append("WHERE cus_phoneNumber LIKE '%" + search + "%'" );
			sql.append(" OR cus_email_address LIKE '%" + search + "%'" );
		}
		sql.append(" ORDER BY cus_id DESC LIMIT " + offset + ", " + limit );
		System.out.println(sql.toString());
		return jdbcTemplate.query( sql.toString(), new CustomerMapper());
	}
	
	public int getFoundRows(){
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tbl_customer", Integer.class);
	}

	public boolean insert(Customer customer) {
		int inserted = jdbcTemplate.update(
		        "INSERT INTO "
		        + "tbl_customer ("
		        + "cus_firstname, "
		        + "cus_lastname, "
		        + "cus_gender, "
		        + "cus_companey,"
		        + "cus_email_address, "
		        + "cus_dob, "
		        + "cus_address, "
		        + "cus_phoneNumber, "
		        + "created_date ) values (?, ?, ?, ?, ?, ?, ?, ?,?)",
		        customer.getFirstname(), 
		        customer.getLastname(), 
		        customer.getGender(),
		        customer.getCompaney(),
		        customer.getEmail(), 
		        customer.getDob(),
		        customer.getAddress(),
		        customer.getPhone(),
		        customer.getCreatedDateToString()
		);	
		
		if(inserted > 0) return true;
		else return false;
	}
	
	public boolean delete(Integer id){
		if(id == null) return false;
		if(jdbcTemplate.update( "DELETE FROM tbl_customer WHERE cus_id = ?", Integer.valueOf(id)) > 0) return true;
		else return false;
	}
	
	public boolean update(final int id, Customer customer) {
		int updated = jdbcTemplate.update(
		        "UPDATE  "
		        + "tbl_customer SET "
		        + "cus_firstname = ?, "
		        + "cus_lastname = ?, "
		        + "cus_gender = ?, "
		        + "cus_companey=?,"
		        + "cus_email_address = ?, "
		        + "cus_dob = ?, "
		        + "cus_address = ?, "
		        + "cus_phoneNumber = ?, "
		        + "updated_date = ?"
		        + "WHERE cus_id = ?",
		        customer.getFirstname(), 
		        customer.getLastname(), 
		        customer.getGender(),
		        customer.getCompaney(),
		        customer.getEmail(), 
		        customer.getDob(),
		        customer.getAddress(),
		        customer.getPhone(),
		        customer.getUpdatedDateToString(),
		        id
		);
		if(updated > 0) return true;
		else return false;
	}

}
  final class CustomerMapper implements RowMapper<Customer> {

	public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
		Customer customer = new Customer();
		customer.setId(rs.getInt("cus_id"));
		customer.setFirstname(rs.getString("cus_firstname"));
		customer.setLastname(rs.getString("cus_lastname"));
		customer.setGender(rs.getString("cus_gender"));
		customer.setCompaney(rs.getString("cus_companey"));
		customer.setEmail(rs.getString("cus_email_address"));
		customer.setAddress(rs.getString("cus_address"));
		customer.setPhone(rs.getString("cus_phoneNumber"));
		customer.setDobFromString(rs.getString("cus_DOB"));
		customer.setCreatedDateFromString(rs.getString("created_date"));
		customer.setUpdatedDateFromString(rs.getString("updated_date"));
		return customer;
	}
}