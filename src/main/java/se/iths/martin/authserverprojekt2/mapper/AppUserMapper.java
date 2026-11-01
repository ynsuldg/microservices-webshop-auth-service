package se.iths.martin.authserverprojekt2.mapper;

import org.mapstruct.Mapper;
import se.iths.martin.authserverprojekt2.dto.AppUserRequestDto;
import se.iths.martin.authserverprojekt2.dto.AppUserResponseDto;
import se.iths.martin.authserverprojekt2.model.AppUser;

/**
 * Omvandlar mellan användarentiteten och API:ets DTO:er.
 */
@Mapper(componentModel = "spring")
public interface AppUserMapper {
    /**
     * Omvandlar inkommande DTO till en entitet.
     *
     * @param appUserRequestDto användarnamn och lösenord från klienten
     * @return den skapade entiteten
     */
    AppUser toEntity(AppUserRequestDto appUserRequestDto);

    /**
     * Omvandlar användaren till en DTO utan lösenord.
     *
     * @param appUser användaren som ska omvandlas
     * @return DTO:n för API-svaret
     */
    AppUserResponseDto toDto(AppUser appUser);
}
