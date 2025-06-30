package com.change.config.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.change.config.aspect.NotificationAspect;
import com.change.config.controller.ConfigController;
import com.change.config.dto.ConfigChangeRequestDto;
import com.change.config.dto.ConfigChangeResponseDto;
import com.change.config.dto.FilterDto;
import com.change.config.mapper.ConfigChangeMapper;
import com.change.config.model.ConfigChange;
import com.change.config.model.ConfigChangeType;
import com.change.config.repository.ConfigChangeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ConfigController.class,
    ConfigChangeService.class,
    NotificationService.class,
    ConfigChangeMapper.class,
    ConfigChangeRepository.class,
    NotificationAspect.class})
public class ConfigControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoSpyBean
  private ConfigChangeService configChangeService;

  @MockitoSpyBean
  private NotificationService notificationService;

  @MockitoSpyBean
  private ConfigChangeMapper configChangeMapper;

  @MockitoSpyBean
  private ConfigChangeRepository configChangeRepository;


  @Test
  public void testCreateConfigChange_Success() throws Exception {
    ConfigChangeRequestDto requestDto = ConfigChangeRequestDto.builder()
        .type(ConfigChangeType.ADD)
        .key("credit.limit")
        .value("5000")
        .description("Set credit limit")
        .critical(false)
        .build();

    mockMvc.perform(post("/api/configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.type").value(requestDto.getType().toString()))
        .andExpect(jsonPath("$.key").value(requestDto.getKey()))
        .andExpect(jsonPath("$.value").value(requestDto.getValue()))
        .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
        .andExpect(jsonPath("$.critical").value(requestDto.isCritical()))
        .andExpect(jsonPath("$.timestamp").exists());

    verify(configChangeMapper).toEntity(any(ConfigChangeRequestDto.class));
    verify(configChangeService).createConfigChange(any(ConfigChange.class));
    verify(configChangeMapper).toDto(any(ConfigChange.class));
    verify(notificationService, never()).notifyCriticalChange(any(ConfigChange.class));
  }

  @Test
  public void testCreateConfigChange_WithCriticalChange_ShouldNotify() throws Exception {
    ConfigChangeRequestDto requestDto = ConfigChangeRequestDto.builder()
        .type(ConfigChangeType.UPDATE)
        .key("approval.policy")
        .value("strict")
        .description("Changed approval policy to strict")
        .critical(true)
        .build();

    mockMvc.perform(post("/api/configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.critical").value(true));

    verify(configChangeMapper).toEntity(any(ConfigChangeRequestDto.class));

    var argCapture = ArgumentCaptor.forClass(ConfigChange.class);
    verify(configChangeService).createConfigChange(argCapture.capture());
    verify(configChangeRepository).save(any(ConfigChange.class));
    verify(configChangeMapper).toDto(any(ConfigChange.class));

    verify(notificationService, times(1)).notifyCriticalChange(argCapture.getValue());
  }


  @Test
  public void testCreateConfigChange_NotificationError_ShouldSave() throws Exception {
    ConfigChangeRequestDto requestDto = ConfigChangeRequestDto.builder()
        .type(ConfigChangeType.UPDATE)
        .key("approval.policy")
        .value("strict")
        .description("Changed approval policy to strict")
        .critical(true)
        .build();
    Mockito.doThrow(RuntimeException.class).when(notificationService).notifyCriticalChange(any(ConfigChange.class));

    mockMvc.perform(post("/api/configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.critical").value(true));

    verify(configChangeMapper).toEntity(any(ConfigChangeRequestDto.class));

    var argCapture = ArgumentCaptor.forClass(ConfigChange.class);
    verify(configChangeRepository).save(argCapture.capture());

    var saved = configChangeRepository.findById(argCapture.getValue().getId());
    assertNotNull(saved);
  }

  @Test
  public void testCreateConfigChange_MissingRequiredFields() throws Exception {
    // Missing type, key, and value which are required
    ConfigChangeRequestDto requestDto = ConfigChangeRequestDto.builder().build();

    mockMvc.perform(post("/api/configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors").exists());

    verify(configChangeMapper, never()).toEntity(any(ConfigChangeRequestDto.class));
    verify(configChangeService, never()).createConfigChange(any(ConfigChange.class));
  }

  @Test
  public void testCreateConfigChange_InvalidType() throws Exception {
    String invalidRequest = "{\"type\":\"INVALID_TYPE\",\"key\":\"credit.limit\",\"value\":\"5000\"}";

    mockMvc.perform(post("/api/configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidRequest))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors").exists());

    verify(configChangeMapper, never()).toEntity(any(ConfigChangeRequestDto.class));
    verify(configChangeService, never()).createConfigChange(any(ConfigChange.class));
  }

  @ParameterizedTest
  @MethodSource("params")
  public void testGetConfigChanges(FilterDto filterDto, List<ConfigChange> allChanges, List<ConfigChange> expectedResult) throws Exception {

    Mockito.when(configChangeRepository.findAll()).thenReturn(allChanges);
    var requestBuilder = get("/api/configs");
    Optional.of(filterDto)
        .map(FilterDto::getStartTime)
        .map(LocalDateTime::toString)
        .ifPresent(value -> requestBuilder.param("startTime", value));

    Optional.of(filterDto)
        .map(FilterDto::getEndTime)
        .map(LocalDateTime::toString)
        .ifPresent(value -> requestBuilder.param("endTime", value));

    Optional.of(filterDto)
        .map(FilterDto::getType)
        .map(Enum::name)
        .ifPresent(value -> requestBuilder.param("type", value));

    mockMvc.perform(requestBuilder.contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

    verify(configChangeMapper, never()).toEntity(any(ConfigChangeRequestDto.class));
    verify(configChangeService, never()).createConfigChange(any(ConfigChange.class));
  }

  public static Stream<Arguments> params() {
    var changeA = ConfigChange.builder()
        .type(ConfigChangeType.UPDATE)
        .timestamp(LocalDateTime.now().minusDays(3))
        .build();
    var changeB = ConfigChange.builder()
        .type(ConfigChangeType.ADD)
        .timestamp(LocalDateTime.now().minusDays(5))
        .build();
    var changeC = ConfigChange.builder()
        .type(ConfigChangeType.DELETE)
        .timestamp(LocalDateTime.now().minusDays(1))
        .build();
    var changeD = ConfigChange.builder()
        .type(ConfigChangeType.DELETE)
        .timestamp(LocalDateTime.now().minusDays(6))
        .build();
    var allChanges = List.of(changeA, changeB, changeC, changeD);

    return Stream.of(
        //no filters
        Arguments.of(FilterDto.builder().build(), allChanges, allChanges),
        // filter by type
        Arguments.of(FilterDto.builder().type(ConfigChangeType.ADD).build(), allChanges, List.of(changeB)),
        //filter by start date
        Arguments.of(FilterDto.builder().startTime(LocalDateTime.now().minusDays(4)).build(), allChanges, List.of(changeA, changeC)),
        //filter by end date
        Arguments.of(FilterDto.builder().endTime(LocalDateTime.now().minusDays(4)).build(), allChanges, List.of(changeB, changeD)),
        //filter by date range
        Arguments.of(FilterDto.builder()
                .startTime(LocalDateTime.now().minusDays(4))
                .endTime(LocalDateTime.now().minusDays(2))
                .build(),
            allChanges, List.of(changeA)),

        //filter by all params
        Arguments.of(FilterDto.builder()
                .startTime(LocalDateTime.now().minusDays(4))
                .endTime(LocalDateTime.now().minusDays(2))
                .type(ConfigChangeType.ADD)
                .build(),
            allChanges, List.of())

    );
  }

  @Test
  public void testGetConfigChangeById() throws Exception {
    var id = "unique id";
    var expected = ConfigChangeResponseDto.builder()
        .id(id)
        .critical(true)
        .description("Changed description")
        .timestamp(LocalDateTime.now())
        .key("approval.policy")
        .value("strict")
        .type(ConfigChangeType.UPDATE)
        .build();
    Mockito.when(configChangeRepository.findById(id))
        .thenReturn(ConfigChange.builder()
            .id(id)
            .type(expected.getType())
            .timestamp(expected.getTimestamp())
            .key(expected.getKey())
            .value(expected.getValue())
            .description(expected.getDescription())
            .critical(expected.isCritical())
            .build());
    mockMvc.perform(get("/api/configs/" + id)
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expected)));

    verify(configChangeMapper).toDto(any(ConfigChange.class));
    verify(configChangeService).getConfigChangeById(id);
  }
}
