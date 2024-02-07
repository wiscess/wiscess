package com.wiscess.security.jdbc;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ReflectionUtils;

public class ReflectionUserSource {

	public Object getUser(UserDetails user) {
		Method userMethod = findUserMethod(user);

		try {
			return userMethod.invoke(user);
		}
		catch (Exception exception) {
			throw new AuthenticationServiceException(exception.getMessage(), exception);
		}
	}
	
	private Method findUserMethod(UserDetails user) {
		String userPropertyToUse = "user";
		Method userMethod = ReflectionUtils.findMethod(user.getClass(),
				userPropertyToUse, new Class[0]);

		if (userMethod == null) {
			PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(user.getClass(),
					userPropertyToUse);

			if (pd != null) {
				userMethod = pd.getReadMethod();
			}

			if (userMethod == null) {
				throw new AuthenticationServiceException(
						"Unable to find user method on user Object. Does the class '"
								+ user.getClass().getName()
								+ "' have a method or getter named '" + userPropertyToUse
								+ "' ?");
			}
		}
		return userMethod;
	}
}
