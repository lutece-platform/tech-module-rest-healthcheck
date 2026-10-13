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

package fr.paris.lutece.plugins.rest.modules.healthcheck.rs;

import java.lang.annotation.Annotation;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import fr.paris.lutece.plugins.rest.modules.healthcheck.service.HealthCheckService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

/**
 * Health check REST resource
 */
@Path( RestConstants.BASE_PATH + Constants.API_PATH + Constants.VERSION_PATH + Constants.HEALTH_PATH )
@ApplicationScoped
public class HealthCheckRest
{
    private static final int VERSION_1 = 1;
    private static final String DATA_MESSAGE = "message";

    @Inject
    private HealthCheckService _healthCheckService;

    /**
     * Get check List results
     * 
     * @param nVersion
     *            the API version
     * @return the Test List resluts
     */
    @GET
    @Path( StringUtils.EMPTY )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getCheckList( @PathParam( Constants.VERSION ) Integer nVersion )
    {
        if ( nVersion == VERSION_1 )
        {
            return getCheckListV1( null );
        }

        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) ).build( );
    }

    /**
     * Get check List results
     * 
     * @param nVersion
     *            the API version
     * @return the Test List resluts
     */
    @GET
    @Path( Constants.READY_PATH )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getReadinessCheckList( @PathParam( Constants.VERSION ) Integer nVersion )
    {
        if ( nVersion == VERSION_1 )
        {
            return getCheckListV1( Constants.READY_PATH );
        }

        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) ).build( );
    }
    
    /**
     * Get check List results
     * 
     * @param nVersion
     *            the API version
     * @return the Test List resluts
     */
    @GET
    @Path( Constants.LIVE_PATH )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getLivenessCheckList( @PathParam( Constants.VERSION ) Integer nVersion )
    {
        if ( nVersion == VERSION_1 )
        {
            return getCheckListV1( Constants.LIVE_PATH );
        }

        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) ).build( );
    }
    
    /**
     * Get check List V1
     * 
     * @param strHealthTypePath
     *            the health type path, or null for every check
     * @return the check List for the version 1
     */
    private Response getCheckListV1( String strHealthTypePath )
    {
        List<HealthCheckResponse> listChecks = _healthCheckService.check( getQualifierForPath( strHealthTypePath ) );

        if ( listChecks.isEmpty( ) )
        {
            return Response.status( Response.Status.SERVICE_UNAVAILABLE )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.SERVICE_UNAVAILABLE.name( ), Constants.ERROR_NO_SERVICE ) ) )
                    .build( );
        }

        HealthCheckResponse downCheck = listChecks.stream( ).filter( t -> ( t.getStatus( ) == HealthCheckResponse.Status.DOWN ) ).findFirst( ).orElse( null );

        if ( downCheck != null )
        {
            String msg = downCheck.getName( );
            if ( downCheck.getData( ).isPresent( ) && downCheck.getData( ).get( ).containsKey( DATA_MESSAGE ) )
            {
                msg = String.valueOf( downCheck.getData( ).get( ).get( DATA_MESSAGE ) );
            }
            return Response.status( Response.Status.SERVICE_UNAVAILABLE )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.SERVICE_UNAVAILABLE.name( ), msg ) ) )
                    .build( );
        }

        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( listChecks.stream( ).map( HealthCheckResult::new ).toList( ) ) ) ).build( );
    }

    /**
     * Get the qualifier of the checks served by a path
     * 
     * @param strHealthType
     *            the health type path
     * @return the qualifier, or null for every check
     */
    private Annotation getQualifierForPath( String strHealthType )
    {
        if ( Constants.READY_PATH.equals( strHealthType ) )
        {
            return Readiness.Literal.INSTANCE;
        }

        if ( Constants.LIVE_PATH.equals( strHealthType ) )
        {
            return Liveness.Literal.INSTANCE;
        }

        return null;
    }
}
