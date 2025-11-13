package com.alineumsoft.zenwk.security.common.util;

import com.alineumsoft.zenwk.security.common.component.AppContextHolderComponent;
import com.alineumsoft.zenwk.security.common.constants.GeneralConstants;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * <b>Clase utilitaria para la gestion del log transaccional o historico</b>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class HistoricalUtil
 */
@Slf4j
public final class HistoricalUtil {
  /**
   * <p>
   * Constructor
   * </p>
   */
  private HistoricalUtil() {

  }

  /**
   * <p>
   * <b> Util historico: </b> Metodo generico para el registro historico
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param operation
   */
  public static <T> void registerHistorical(T entity, HistoricalOperationEnum operation,
      Class<?> classService) {
    try {
      Object targetService = AppContextHolderComponent.getBean(classService);
      targetService.getClass().getMethod(GeneralConstants.METHOD_SAVE_HISTORICAL, entity.getClass(),
          HistoricalOperationEnum.class).invoke(targetService, entity, operation);

    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(CoreExceptionEnum.TECH_COMMON_HISTORICAL_ENTITY_NOT_FOUND
          .getCodeMessage(entity.getClass().getSimpleName()).concat(e.getMessage()));
    }
  }

}
