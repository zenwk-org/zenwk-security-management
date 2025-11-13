package com.alineumsoft.zenwk.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alineumsoft.zenwk.security.entity.LogSecurity;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 */
public interface LogSecurityRepository extends JpaRepository<LogSecurity, Long> {

}
