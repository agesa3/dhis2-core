package org.hisp.dhis.dataelement;

/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.category.Category;
import org.hisp.dhis.category.CategoryCombo;
import org.hisp.dhis.category.CategoryComboStore;
import org.hisp.dhis.category.CategoryOption;
import org.hisp.dhis.category.CategoryOptionCombo;
import org.hisp.dhis.category.CategoryOptionComboStore;
import org.hisp.dhis.category.CategoryOptionGroup;
import org.hisp.dhis.category.CategoryOptionGroupSet;
import org.hisp.dhis.category.CategoryOptionGroupSetStore;
import org.hisp.dhis.category.CategoryOptionGroupStore;
import org.hisp.dhis.category.CategoryOptionStore;
import org.hisp.dhis.category.CategoryService;
import org.hisp.dhis.category.CategoryStore;
import org.hisp.dhis.common.DataDimensionType;
import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.IdentifiableProperty;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetElement;
import org.hisp.dhis.security.acl.AccessStringHelper;
import org.hisp.dhis.security.acl.AclService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Abyot Asalefew
 */
@Transactional
public class DefaultCategoryService
    implements CategoryService
{
    private static final Log log = LogFactory.getLog( DefaultCategoryService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CategoryStore categoryStore;

    public void setCategoryStore( CategoryStore categoryStore )
    {
        this.categoryStore = categoryStore;
    }

    private CategoryOptionStore categoryOptionStore;

    public void setCategoryOptionStore( CategoryOptionStore categoryOptionStore )
    {
        this.categoryOptionStore = categoryOptionStore;
    }

    private CategoryComboStore categoryComboStore;

    public void setCategoryComboStore( CategoryComboStore categoryComboStore )
    {
        this.categoryComboStore = categoryComboStore;
    }

    private CategoryOptionComboStore categoryOptionComboStore;

    public void setCategoryOptionComboStore( CategoryOptionComboStore categoryOptionComboStore )
    {
        this.categoryOptionComboStore = categoryOptionComboStore;
    }

    private CategoryOptionGroupStore categoryOptionGroupStore;

    public void setCategoryOptionGroupStore( CategoryOptionGroupStore categoryOptionGroupStore )
    {
        this.categoryOptionGroupStore = categoryOptionGroupStore;
    }

    private CategoryOptionGroupSetStore categoryOptionGroupSetStore;

    public void setCategoryOptionGroupSetStore( CategoryOptionGroupSetStore categoryOptionGroupSetStore )
    {
        this.categoryOptionGroupSetStore = categoryOptionGroupSetStore;
    }

    private IdentifiableObjectManager idObjectManager;

    public void setIdObjectManager( IdentifiableObjectManager idObjectManager )
    {
        this.idObjectManager = idObjectManager;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private AclService aclService;

    public void setAclService( AclService aclService )
    {
        this.aclService = aclService;
    }

    // -------------------------------------------------------------------------
    // Category
    // -------------------------------------------------------------------------

    @Override
    public int addCategory( Category dataElementCategory )
    {
        categoryStore.save( dataElementCategory );

        return dataElementCategory.getId();
    }

    @Override
    public void updateCategory( Category dataElementCategory )
    {
        categoryStore.update( dataElementCategory );
    }

    @Override
    public void deleteCategory( Category dataElementCategory )
    {
        categoryStore.delete( dataElementCategory );
    }

    @Override
    public List<Category> getAllDataElementCategories()
    {
        return categoryStore.getAll();
    }

    @Override
    public Category getCategory( int id )
    {
        return categoryStore.get( id );
    }

    @Override
    public Category getCategory( String uid )
    {
        return categoryStore.getByUid( uid );
    }

    @Override
    public Category getCategoryByName( String name )
    {
        List<Category> dataElementCategories = new ArrayList<>(
            categoryStore.getAllEqName( name ) );

        if ( dataElementCategories.isEmpty() )
        {
            return null;
        }

        return dataElementCategories.get( 0 );
    }

    @Override
    public Category getDefaultCategory()
    {
        return getCategoryByName( Category.DEFAULT_NAME );
    }

    @Override
    public List<Category> getDisaggregationCategories()
    {
        return categoryStore.getCategoriesByDimensionType( DataDimensionType.DISAGGREGATION );
    }

    @Override
    public List<Category> getDisaggregationDataDimensionCategoriesNoAcl()
    {
        return categoryStore.getCategoriesNoAcl( DataDimensionType.DISAGGREGATION, true );
    }

    @Override
    public List<Category> getAttributeCategories()
    {
        return categoryStore.getCategoriesByDimensionType( DataDimensionType.ATTRIBUTE );
    }

    @Override
    public List<Category> getAttributeDataDimensionCategoriesNoAcl()
    {
        return categoryStore.getCategoriesNoAcl( DataDimensionType.ATTRIBUTE, true );
    }

    // -------------------------------------------------------------------------
    // CategoryOption
    // -------------------------------------------------------------------------

    @Override
    public int addCategoryOption( CategoryOption dataElementCategoryOption )
    {
        categoryOptionStore.save( dataElementCategoryOption );

        return dataElementCategoryOption.getId();
    }

    @Override
    public void updateCategoryOption( CategoryOption dataElementCategoryOption )
    {
        categoryOptionStore.update( dataElementCategoryOption );
    }

    @Override
    public void deleteCategoryOption( CategoryOption dataElementCategoryOption )
    {
        categoryOptionStore.delete( dataElementCategoryOption );
    }

    @Override
    public CategoryOption getCategoryOption( int id )
    {
        return categoryOptionStore.get( id );
    }

    @Override
    public CategoryOption getCategoryOption( String uid )
    {
        return categoryOptionStore.getByUid( uid );
    }

    @Override
    public CategoryOption getCategoryOptionByName( String name )
    {
        return categoryOptionStore.getByName( name );
    }

    @Override
    public CategoryOption getDefaultCategoryOption()
    {
        return getCategoryOptionByName( CategoryOption.DEFAULT_NAME );
    }

    @Override
    public List<CategoryOption> getAllCategoryOptions()
    {
        return categoryOptionStore.getAll();
    }

    @Override
    public List<CategoryOption> getCategoryOptions( Category category )
    {
        return categoryOptionStore.getCategoryOptions( category );
    }

    @Override
    public Set<CategoryOption> getCoDimensionConstraints( UserCredentials userCredentials )
    {
        Set<CategoryOption> options = null;

        Set<Category> catConstraints = userCredentials.getCatDimensionConstraints();

        if ( catConstraints != null && !catConstraints.isEmpty() )
        {
            options = new HashSet<>();

            for ( Category category : catConstraints )
            {
                options.addAll( getCategoryOptions( category ) );
            }
        }

        return options;
    }

    // -------------------------------------------------------------------------
    // CategoryCombo
    // -------------------------------------------------------------------------

    @Override
    public int addCategoryCombo( CategoryCombo dataElementCategoryCombo )
    {
        categoryComboStore.save( dataElementCategoryCombo );

        return dataElementCategoryCombo.getId();
    }

    @Override
    public void updateCategoryCombo( CategoryCombo dataElementCategoryCombo )
    {
        categoryComboStore.update( dataElementCategoryCombo );
    }

    @Override
    public void deleteCategoryCombo( CategoryCombo dataElementCategoryCombo )
    {
        categoryComboStore.delete( dataElementCategoryCombo );
    }

    @Override
    public List<CategoryCombo> getAllCategoryCombos()
    {
        return categoryComboStore.getAll();
    }

    @Override
    public CategoryCombo getCategoryCombo( int id )
    {
        return categoryComboStore.get( id );
    }

    @Override
    public CategoryCombo getCategoryCombo( String uid )
    {
        return categoryComboStore.getByUid( uid );
    }

    @Override
    public CategoryCombo getCategoryComboByName( String name )
    {
        return categoryComboStore.getByName( name );
    }

    @Override
    public CategoryCombo getDefaultCategoryCombo()
    {
        return getCategoryComboByName( CategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
    }

    @Override
    public List<CategoryCombo> getDisaggregationCategoryCombos()
    {
        return categoryComboStore.getCategoryCombosByDimensionType( DataDimensionType.DISAGGREGATION );
    }

    @Override
    public List<CategoryCombo> getAttributeCategoryCombos()
    {
        return categoryComboStore.getCategoryCombosByDimensionType( DataDimensionType.ATTRIBUTE );
    }

    @Override
    public String validateCategoryCombo( CategoryCombo categoryCombo )
    {
        if ( categoryCombo == null )
        {
            return "category_combo_is_null";
        }

        if ( categoryCombo.getCategories() == null || categoryCombo.getCategories().isEmpty() )
        {
            return "category_combo_must_have_at_least_one_category";
        }

        if ( Sets.newHashSet( categoryCombo.getCategories() ).size() < categoryCombo.getCategories().size() )
        {
            return "category_combo_cannot_have_duplicate_categories";
        }

        Set<CategoryOption> categoryOptions = new HashSet<CategoryOption>();

        for ( Category category : categoryCombo.getCategories() )
        {
            if ( category == null || category.getCategoryOptions().isEmpty() )
            {
                return "categories_must_have_at_least_one_category_option";
            }

            if ( !Sets.intersection( categoryOptions, Sets.newHashSet( category.getCategoryOptions() ) ).isEmpty() )
            {
                return "categories_cannot_share_category_options";
            }
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // CategoryOptionCombo
    // -------------------------------------------------------------------------

    @Override
    public int addCategoryOptionCombo( CategoryOptionCombo dataElementCategoryOptionCombo )
    {
        categoryOptionComboStore.save( dataElementCategoryOptionCombo );

        return dataElementCategoryOptionCombo.getId();
    }

    @Override
    public void updateCategoryOptionCombo( CategoryOptionCombo dataElementCategoryOptionCombo )
    {
        categoryOptionComboStore.update( dataElementCategoryOptionCombo );
    }

    @Override
    public void deleteCategoryOptionCombo( CategoryOptionCombo dataElementCategoryOptionCombo )
    {
        categoryOptionComboStore.delete( dataElementCategoryOptionCombo );
    }

    @Override
    @Transactional( noRollbackFor = DeleteNotAllowedException.class )
    public void deleteCategoryOptionComboNoRollback( CategoryOptionCombo categoryOptionCombo )
    {
        categoryOptionComboStore.delete( categoryOptionCombo );
    }

    @Override
    public CategoryOptionCombo getCategoryOptionCombo( int id )
    {
        return categoryOptionComboStore.get( id );
    }

    @Override
    public CategoryOptionCombo getCategoryOptionCombo( String uid )
    {
        return categoryOptionComboStore.getByUid( uid );
    }

    @Override
    public CategoryOptionCombo getCategoryOptionComboByCode( String code )
    {
        return categoryOptionComboStore.getByCode( code );
    }

    @Override
    public CategoryOptionCombo getCategoryOptionCombo(
        Collection<CategoryOption> categoryOptions )
    {
        for ( CategoryOptionCombo categoryOptionCombo : getAllCategoryOptionCombos() )
        {
            if ( CollectionUtils.isEqualCollection( categoryOptions, categoryOptionCombo.getCategoryOptions() ) )
            {
                return categoryOptionCombo;
            }
        }

        return null;
    }

    @Override
    public CategoryOptionCombo getCategoryOptionCombo( CategoryCombo categoryCombo,
        Set<CategoryOption> categoryOptions )
    {
        return categoryOptionComboStore.getCategoryOptionCombo( categoryCombo, categoryOptions );
    }

    @Override
    public List<CategoryOptionCombo> getAllCategoryOptionCombos()
    {
        return categoryOptionComboStore.getAll();
    }

    @Override
    public void generateDefaultDimension()
    {
        // ---------------------------------------------------------------------
        // CategoryOption
        // ---------------------------------------------------------------------

        CategoryOption categoryOption = new CategoryOption( CategoryOption.DEFAULT_NAME );
        categoryOption.setUid( "xYerKDKCefk" );
        categoryOption.setCode( "default" );

        addCategoryOption( categoryOption );

        categoryOption.setPublicAccess( AccessStringHelper.CATEGORY_OPTION_DEFAULT );
        updateCategoryOption( categoryOption );

        // ---------------------------------------------------------------------
        // Category
        // ---------------------------------------------------------------------

        Category category = new Category( Category.DEFAULT_NAME, DataDimensionType.DISAGGREGATION );
        category.setUid( "GLevLNI9wkl" );
        category.setCode( "default" );
        category.setDataDimension( false );

        category.addCategoryOption( categoryOption );
        addCategory( category );

        category.setPublicAccess( AccessStringHelper.CATEGORY_NO_DATA_SHARING_DEFAULT );
        updateCategory( category );

        // ---------------------------------------------------------------------
        // CategoryCombo
        // ---------------------------------------------------------------------

        CategoryCombo categoryCombo = new CategoryCombo( CategoryCombo.DEFAULT_CATEGORY_COMBO_NAME, DataDimensionType.DISAGGREGATION );
        categoryCombo.setUid( "bjDvmb4bfuf" );
        categoryCombo.setCode( "default" );
        categoryCombo.setDataDimensionType( DataDimensionType.DISAGGREGATION );

        categoryCombo.addCategory( category );
        addCategoryCombo( categoryCombo );

        categoryCombo.setPublicAccess( AccessStringHelper.CATEGORY_NO_DATA_SHARING_DEFAULT );
        updateCategoryCombo( categoryCombo );

        // ---------------------------------------------------------------------
        // CategoryOptionCombo
        // ---------------------------------------------------------------------

        CategoryOptionCombo categoryOptionCombo = new CategoryOptionCombo();
        categoryOptionCombo.setUid( "HllvX50cXC0" );
        categoryOptionCombo.setCode( "default" );

        categoryOptionCombo.setCategoryCombo( categoryCombo );
        categoryOptionCombo.addCategoryOption( categoryOption );

        addCategoryOptionCombo( categoryOptionCombo );

        categoryOptionCombo.setPublicAccess( AccessStringHelper.CATEGORY_NO_DATA_SHARING_DEFAULT );
        updateCategoryOptionCombo( categoryOptionCombo );

        Set<CategoryOptionCombo> categoryOptionCombos = new HashSet<>();
        categoryOptionCombos.add( categoryOptionCombo );
        categoryCombo.setOptionCombos( categoryOptionCombos );

        updateCategoryCombo( categoryCombo );

        categoryOption.setCategoryOptionCombos( categoryOptionCombos );
        updateCategoryOption( categoryOption );
    }

    @Override
    public CategoryOptionCombo getDefaultCategoryOptionCombo()
    {
        CategoryCombo categoryCombo = getCategoryComboByName( CategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        return categoryCombo != null && categoryCombo.hasOptionCombos() ? categoryCombo.getOptionCombos().iterator().next() : null;
    }

    @Override
    public void generateOptionCombos( CategoryCombo categoryCombo )
    {
        categoryCombo.generateOptionCombos();

        for ( CategoryOptionCombo optionCombo : categoryCombo.getOptionCombos() )
        {
            categoryCombo.getOptionCombos().add( optionCombo );
            addCategoryOptionCombo( optionCombo );
        }

        updateCategoryCombo( categoryCombo );
    }

    @Override
    public void updateOptionCombos( Category category )
    {
        for ( CategoryCombo categoryCombo : getAllCategoryCombos() )
        {
            if ( categoryCombo.getCategories().contains( category ) )
            {
                updateOptionCombos( categoryCombo );
            }
        }
    }

    @Override
    public void updateOptionCombos( CategoryCombo categoryCombo )
    {
        if ( categoryCombo == null || !categoryCombo.isValid() )
        {
            log.warn( "Category combo is null or invalid, could not update option combos: " + categoryCombo );
            return;
        }

        List<CategoryOptionCombo> generatedOptionCombos = categoryCombo.generateOptionCombosList();
        Set<CategoryOptionCombo> persistedOptionCombos = categoryCombo.getOptionCombos();

        boolean modified = false;

        for ( CategoryOptionCombo optionCombo : generatedOptionCombos )
        {
            if ( !persistedOptionCombos.contains( optionCombo ) )
            {
                categoryCombo.getOptionCombos().add( optionCombo );
                addCategoryOptionCombo( optionCombo );

                log.info( "Added missing category option combo: " + optionCombo + " for category combo: "
                    + categoryCombo.getName() );
                modified = true;
            }
        }

        if ( modified )
        {
            updateCategoryCombo( categoryCombo );
        }
    }


    @Override
    public CategoryOptionCombo getCategoryOptionComboAcl( IdentifiableProperty property, String id )
    {
        CategoryOptionCombo coc = idObjectManager.getObject( CategoryOptionCombo.class, property, id );

        if ( coc != null )
        {
            User user = currentUserService.getCurrentUser();

            for ( CategoryOption categoryOption : coc.getCategoryOptions() )
            {
                if ( !aclService.canDataWrite( user, categoryOption ) )
                {
                    return null;
                }
            }
        }

        return coc;
    }

    @Override
    public void updateCategoryOptionComboNames()
    {
        categoryOptionComboStore.updateNames();
    }

    // -------------------------------------------------------------------------
    // DataElementOperand
    // -------------------------------------------------------------------------

    @Override
    public List<DataElementOperand> getOperands( Collection<DataElement> dataElements )
    {
        return getOperands( dataElements, false );
    }

    @Override
    public List<DataElementOperand> getOperands( Collection<DataElement> dataElements, boolean includeTotals )
    {
        List<DataElementOperand> operands = Lists.newArrayList();

        for ( DataElement dataElement : dataElements )
        {
            Set<CategoryCombo> categoryCombos = dataElement.getCategoryCombos();

            boolean anyIsDefault = categoryCombos.stream().anyMatch( cc -> cc.isDefault() );

            if ( includeTotals && !anyIsDefault )
            {
                operands.add( new DataElementOperand( dataElement ) );
            }

            for ( CategoryCombo categoryCombo : categoryCombos )
            {
                operands.addAll( getOperands( dataElement, categoryCombo ) );
            }
        }

        return operands;
    }

    @Override
    public List<DataElementOperand> getOperands( DataSet dataSet, boolean includeTotals )
    {
        List<DataElementOperand> operands = Lists.newArrayList();

        for ( DataSetElement element : dataSet.getDataSetElements() )
        {
            CategoryCombo categoryCombo = element.getResolvedCategoryCombo();

            if ( includeTotals && !categoryCombo.isDefault() )
            {
                operands.add( new DataElementOperand( element.getDataElement() ) );
            }

            operands.addAll( getOperands( element.getDataElement(), element.getResolvedCategoryCombo() ) );
        }

        return operands;
    }

    private List<DataElementOperand> getOperands( DataElement dataElement, CategoryCombo categoryCombo )
    {
        List<DataElementOperand> operands = Lists.newArrayList();

        for ( CategoryOptionCombo categoryOptionCombo : categoryCombo.getSortedOptionCombos() )
        {
            operands.add( new DataElementOperand( dataElement, categoryOptionCombo ) );
        }

        return operands;
    }

    // -------------------------------------------------------------------------
    // CategoryOptionGroup
    // -------------------------------------------------------------------------

    @Override
    public int saveCategoryOptionGroup( CategoryOptionGroup group )
    {
        categoryOptionGroupStore.save( group );

        return group.getId();
    }

    @Override
    public void updateCategoryOptionGroup( CategoryOptionGroup group )
    {
        categoryOptionGroupStore.update( group );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroup( int id )
    {
        return categoryOptionGroupStore.get( id );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroup( String uid )
    {
        return categoryOptionGroupStore.getByUid( uid );
    }

    @Override
    public void deleteCategoryOptionGroup( CategoryOptionGroup group )
    {
        categoryOptionGroupStore.delete( group );
    }

    @Override
    public List<CategoryOptionGroup> getAllCategoryOptionGroups()
    {
        return categoryOptionGroupStore.getAll();
    }

    @Override
    public List<CategoryOptionGroup> getCategoryOptionGroups( CategoryOptionGroupSet groupSet )
    {
        return categoryOptionGroupStore.getCategoryOptionGroups( groupSet );
    }

    @Override
    public Set<CategoryOptionGroup> getCogDimensionConstraints( UserCredentials userCredentials )
    {
        Set<CategoryOptionGroup> groups = null;

        Set<CategoryOptionGroupSet> cogsConstraints = userCredentials.getCogsDimensionConstraints();

        if ( cogsConstraints != null && !cogsConstraints.isEmpty() )
        {
            groups = new HashSet<>();

            for ( CategoryOptionGroupSet cogs : cogsConstraints )
            {
                groups.addAll( getCategoryOptionGroups( cogs ) );
            }
        }

        return groups;
    }

    // -------------------------------------------------------------------------
    // CategoryOptionGroupSet
    // -------------------------------------------------------------------------

    @Override
    public int saveCategoryOptionGroupSet( CategoryOptionGroupSet group )
    {
        categoryOptionGroupSetStore.save( group );

        return group.getId();
    }

    @Override
    public void updateCategoryOptionGroupSet( CategoryOptionGroupSet group )
    {
        categoryOptionGroupSetStore.update( group );
    }

    @Override
    public CategoryOptionGroupSet getCategoryOptionGroupSet( int id )
    {
        return categoryOptionGroupSetStore.get( id );
    }

    @Override
    public CategoryOptionGroupSet getCategoryOptionGroupSet( String uid )
    {
        return categoryOptionGroupSetStore.getByUid( uid );
    }

    @Override
    public void deleteCategoryOptionGroupSet( CategoryOptionGroupSet group )
    {
        categoryOptionGroupSetStore.delete( group );
    }

    @Override
    public List<CategoryOptionGroupSet> getAllCategoryOptionGroupSets()
    {
        return categoryOptionGroupSetStore.getAll();
    }

    @Override
    public List<CategoryOptionGroupSet> getDisaggregationCategoryOptionGroupSetsNoAcl()
    {
        return categoryOptionGroupSetStore.getCategoryOptionGroupSetsNoAcl( DataDimensionType.DISAGGREGATION, true );
    }

    @Override
    public List<CategoryOptionGroupSet> getAttributeCategoryOptionGroupSetsNoAcl()
    {
        return categoryOptionGroupSetStore.getCategoryOptionGroupSetsNoAcl( DataDimensionType.ATTRIBUTE, true );
    }
}
