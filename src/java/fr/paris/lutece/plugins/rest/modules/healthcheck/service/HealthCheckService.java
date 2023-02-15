/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.rest.modules.healthcheck.service;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import fr.paris.lutece.portal.service.spring.SpringContextService;

public class HealthCheckService
{

    private static HealthCheckService _singleton;

    private static List<HealthCheck> _healthCheckList;

    /**
     * Private constructor
     */
    private HealthCheckService( )
    {
        _healthCheckList = SpringContextService.getBeansOfType( HealthCheck.class );
    }

    /**
     * Get the unique instance of the Security Service
     * 
     * @return The instance
     */
    public static HealthCheckService getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new HealthCheckService( );
        }

        return _singleton;
    }

    /**
     * get health checks
     * @param <T>
     * 
     * @return a list of test results
     */
    public List<HealthCheckResponse> check( Class<? extends Annotation> annonationClass )
    {
        List<HealthCheckResponse> list = new ArrayList<>( );

        for ( HealthCheck health : _healthCheckList )
        {
        	if ( annonationClass == null || health.getClass( ).isAnnotationPresent( annonationClass ) )
        	{
        		list.add( health.call( ) );
        	}
        }

        return list;
    }
    
    /**
     * get health checks
     * 
     * @return a list of test results
     */
    public List<HealthCheckResponse> check( )
    {
        return check( null );
    }

}
